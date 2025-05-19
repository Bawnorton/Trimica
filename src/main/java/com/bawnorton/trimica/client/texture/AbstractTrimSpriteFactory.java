package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.client.palette.AnimatedTrimPalette;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class AbstractTrimSpriteFactory implements RuntimeTrimSpriteFactory {
    protected final int width;
    protected final int height;

    protected AbstractTrimSpriteFactory(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public SpriteContents create(ResourceLocation texture, ArmorTrim trim, @Nullable DataComponentGetter componentGetter) {
        if(trim == null) {
            return new SpriteContents(texture, new FrameSize(width, height), empty(), ResourceMetadata.EMPTY);
        }
        TrimSpriteMetadata metadata = getSpriteMetadata(trim, componentGetter, texture);
        if(metadata == null) {
            return new SpriteContents(texture, new FrameSize(width, height), empty(), ResourceMetadata.EMPTY);
        }
        NativeImage image = createImageFromMetadata(metadata);
        ResourceMetadata resourceMetadata;
        if(metadata.isAnimated()) {
            resourceMetadata = new ResourceMetadata() {
                @SuppressWarnings("unchecked")
                @Override
                public <T> @NotNull Optional<T> getSection(@NotNull MetadataSectionType<T> metadataSectionType) {
                    if(metadataSectionType.equals(AnimationMetadataSection.TYPE)) {
                        return Optional.of((T) new AnimationMetadataSection(
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                1,
                                false
                        ));
                    }
                    return Optional.empty();
                }
            };
        } else {
            resourceMetadata = ResourceMetadata.EMPTY;
        }
        return new SpriteContents(texture, new FrameSize(width, height), image, resourceMetadata);
    }

    @Nullable
    protected abstract TrimSpriteMetadata getSpriteMetadata(ArmorTrim trim, @Nullable DataComponentGetter componentGetter, ResourceLocation texture);

    protected abstract NativeImage createImageFromMetadata(TrimSpriteMetadata metadata);

    protected NativeImage createColouredImage(TrimSpriteMetadata metadata, TextureContents contents) {
        TrimPalette palette = metadata.palette();
        NativeImage coloured;
        if (palette.isAnimated()) {
            coloured = createColouredPatternAnimation(contents.image(), palette.asAnimated());
        } else {
            coloured = createColouredPatternImage(contents.image(), palette.getColours(), palette.isBuiltin());
        }
        contents.close();
        return coloured;
    }

    protected NativeImage createColouredPatternImage(NativeImage greyscalePatternImage, List<Integer> colours, boolean builtin) {
        NativeImage coloured = new NativeImage(width, height, false);
        IntList[] colourPositions = new IntList[256];

        int count = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = greyscalePatternImage.getPixel(x, y) & 0xFF;
                IntList positions = colourPositions[pixel];
                if (positions == null) {
                    positions = new IntArrayList();
                    colourPositions[pixel] = positions;
                    count++;
                }
                positions.add(x);
                positions.add(y);
            }
        }

        int paletteIndex = count - 1;
        for (int colour = 0; colour < 256; colour++) {
            IntList positions = colourPositions[colour];
            if (positions == null) continue;

            int paletteColour;

            if (colour == 0) {
                paletteColour = 0;
            } else {
                if (paletteIndex > 0) {
                    paletteColour = ARGB.toABGR(0xFF000000 | colours.get(paletteIndex));
                } else {
                    paletteColour = 0;
                }
                paletteIndex--;
            }

            for (int j = 0; j < positions.size(); j += 2) {
                int x = positions.getInt(j);
                int y = positions.getInt(j + 1);
                coloured.setPixel(x, y, builtin ? paletteColour : applyGrayscaleMask(paletteColour, colour));
            }
        }
        return coloured;
    }

    protected NativeImage createColouredPatternAnimation(NativeImage image, AnimatedTrimPalette palette) {
        List<List<Integer>> frames = getFrames(palette);
        int frameHeight = frames.size();
        int frameWidth = image.getWidth();
        int totalHeight = frameHeight * image.getHeight();
        NativeImage stitchedImage = new NativeImage(frameWidth, totalHeight, false);
        for (int frameCount = 0; frameCount < frames.size(); frameCount++) {
            List<Integer> frame = frames.get(frameCount);
            NativeImage coloured = createColouredPatternImage(image, frame, false);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int pixel = coloured.getPixel(x, y);
                    stitchedImage.setPixel(x, y + (frameCount * image.getHeight()), pixel);
                }
            }
            coloured.close();
        }
        return stitchedImage;
    }

    private @NotNull List<List<Integer>> getFrames(AnimatedTrimPalette palette) {
        List<Integer> interpolatedColours = palette.getAnimationColours();
        List<List<Integer>> frames = new ArrayList<>();
        for (int i = 0; i < AnimatedTrimPalette.ANIMATED_PALETTE_SIZE; i++) {
            List<Integer> frame = new ArrayList<>();
            for(int j = 0; j < TrimPalette.PALETTE_SIZE; j++) {
                int index = (i + j) % interpolatedColours.size();
                int colour = interpolatedColours.get(index);
                frame.add(colour);
            }
            frames.add(frame);
        }
        return frames;
    }

    private int applyGrayscaleMask(int colorABGR, int maskABGR) {
        int brightness = maskABGR & 0xFF;

        int alpha = (colorABGR >> 24) & 0xFF;
        int blue  = (colorABGR >> 16) & 0xFF;
        int green = (colorABGR >> 8) & 0xFF;
        int red   = colorABGR & 0xFF;

        if (brightness < 128) {
            red   = (red * brightness + 64) >> 7;
            green = (green * brightness + 64) >> 7;
            blue  = (blue * brightness + 64) >> 7;
        }

        return (alpha << 24) | (blue << 16) | (green << 8) | red;
    }

    protected NativeImage empty() {
        NativeImage image = new NativeImage(width, height, false);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setPixel(x, y, 0);
            }
        }
        return image;
    }
}
