package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.client.texture.palette.TrimPalette;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractTrimSpriteFactory implements RuntimeTrimSpriteFactory {
    protected final int width;
    protected final int height;
    private final float mimicSize;
    
    protected AbstractTrimSpriteFactory(int width, int height, float mimicSize) {
        this.width = width;
        this.height = height;
        this.mimicSize = mimicSize;
    }

    @Override
    public SpriteContents create(ResourceLocation texture, ArmorTrim trim, @Nullable DataComponentGetter componentGetter) {
        if(trim == null) {
            return new SpriteContents(texture, new FrameSize(width, height), empty(), ResourceMetadata.EMPTY);
        }
        NativeImage image = createImageFromMaterial(trim, componentGetter, texture);
        return new SpriteContents(texture, new FrameSize(width, height), image, ResourceMetadata.EMPTY);
    }

    @Override
    public float getMimicSize() {
        return mimicSize;
    }

    protected abstract NativeImage createImageFromMaterial(ArmorTrim trim, DataComponentGetter componentGetter, ResourceLocation location);
    
    protected NativeImage createColouredPatternImage(NativeImage greyscalePatternImage, List<Integer> colours, boolean builtin) {
        NativeImage coloured = new NativeImage(width, height, false);
        Map<Integer, Set<Vector2i>> colourPositions = new Int2ObjectAVLTreeMap<>(Comparator.<Integer>comparingInt(i -> i).reversed());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = greyscalePatternImage.getPixel(x, y);
                colourPositions.computeIfAbsent(pixel, k -> new HashSet<>()).add(new Vector2i(x, y));
            }
        }
        int index = 0;
        for (Map.Entry<Integer, Set<Vector2i>> entry : colourPositions.entrySet()) {
            int colour = entry.getKey();
            int paletteColour;
            if (colour == 0 || index >= TrimPalette.PALETTE_SIZE) {
                paletteColour = 0;
            } else {
                paletteColour = ARGB.toABGR(0xFF000000 | colours.get(index));
            }
            Set<Vector2i> positions = entry.getValue();
            for (Vector2i position : positions) {
                int x = position.x();
                int y = position.y();
                coloured.setPixel(x, y, builtin ? paletteColour : applyGrayscaleMask(paletteColour, colour));
            }
            index++;
        }
        return coloured;
    }

    private int applyGrayscaleMask(int colorABGR, int maskABGR) {
        int brightness = maskABGR & 0xFF;
        float factor = Math.min(1, brightness / 128f);

        int alpha = (colorABGR >> 24) & 0xFF;
        int blue  = (colorABGR >> 16) & 0xFF;
        int green = (colorABGR >> 8) & 0xFF;
        int red   = colorABGR & 0xFF;

        red   = Math.round(red * factor);
        green = Math.round(green * factor);
        blue  = Math.round(blue * factor);

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
