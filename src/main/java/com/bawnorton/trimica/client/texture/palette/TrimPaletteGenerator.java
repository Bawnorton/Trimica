package com.bawnorton.trimica.client.texture.palette;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.mixin.accessor.SpriteContentsAccessor;
import com.bawnorton.trimica.client.texture.colour.ColourGroup;
import com.bawnorton.trimica.client.texture.colour.ColourHSB;
import com.bawnorton.trimica.client.texture.colour.OkLabHelper;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TrimPaletteGenerator {
    private static final Map<ResourceLocation, TrimPalette> TRIM_PALETTES = new HashMap<>();

    public TrimPalette generatePalette(ResourceLocation location, List<BakedQuad> quads) {
        return TRIM_PALETTES.computeIfAbsent(location, key -> {
            List<Integer> colours = getColoursFromQuads(quads);
            if(colours.isEmpty()) {
                Trimica.LOGGER.warn("Trim palette colour could not determined for {}", location);
                return TrimPalette.DEFAULT;
            }
            colours = getDominantColours(colours);
            colours = sortPalette(colours);
            colours = stretchPalette(colours);
            return new TrimPalette(colours);
        });
    }

    private List<Integer> getDominantColours(List<Integer> colours) {
        List<ColourHSB> hsbColours = ColourHSB.fromRGB(colours);

        List<ColourGroup> groups = new ArrayList<>();
        for (ColourHSB colour : hsbColours) {
            boolean foundGroup = false;
            for (ColourGroup group : groups) {
                if (group.isSimilar(colour)) {
                    group.addMember(colour);
                    foundGroup = true;
                    break;
                }
            }
            if (!foundGroup) {
                groups.add(new ColourGroup(colour));
            }
        }
        groups.sort(Comparator.comparingInt(ColourGroup::getWeight).reversed());
        List<ColourHSB> dominantColours = new ArrayList<>();
        int count = 0;
        for (ColourGroup group : groups) {
            if(count < TrimPalette.PALETTE_SIZE) {
                dominantColours.add(group.getRepresentative());
                count++;
            } else {
                break;
            }
        }
        List<Integer> dominantRGB = new ArrayList<>();
        for (ColourHSB colour : dominantColours) {
            dominantRGB.add(colour.colour());
        }
        return dominantRGB;
    }

    private List<Integer> sortPalette(List<Integer> colours) {
        List<ColourHSB> hsbColours = ColourHSB.fromRGB(colours);

        return hsbColours.stream()
                         .sorted(Comparator.comparingDouble(ColourHSB::hue).thenComparing(ColourHSB::saturation).thenComparing(ColourHSB::brightness))
                         .map(ColourHSB::colour)
                         .toList();
    }

    private List<Integer> stretchPalette(List<Integer> palette) {
        int size = palette.size();
        int targetSize = TrimPalette.PALETTE_SIZE;
        if (size >= targetSize) {
            return palette;
        }

        List<double[]> oklabPalette = new ArrayList<>();
        for (int rgb : palette) {
            double[] oklab = OkLabHelper.rgbToOklab(rgb);
            oklabPalette.add(oklab);
        }

        List<double[]> stretchedOKLab = OkLabHelper.strechOkLab(targetSize, size, oklabPalette);

        List<Integer> stretchedPalette = new ArrayList<>(targetSize);
        for (double[] oklab : stretchedOKLab) {
            int rgb = OkLabHelper.oklabToRgb(oklab);
            stretchedPalette.add(rgb);
        }

        return stretchedPalette;
    }

    private @NotNull List<Integer> getColoursFromQuads(List<BakedQuad> quads) {
        List<Integer> colours = new ArrayList<>(quads.size() * 16 * 16);
        for (BakedQuad bakedQuad : quads) {
            int[] colourData = extractColours(bakedQuad.sprite());
            for (int colour : colourData) {
                colours.add(colour);
            }
        }
        return colours.stream().filter(i -> i != 0).toList();
    }

    @SuppressWarnings("resource")
    private int[] extractColours(TextureAtlasSprite sprite) {
        NativeImage spriteImage = ((SpriteContentsAccessor) sprite.contents()).trimica$originalImage();
        int width = spriteImage.getWidth();
        int height = spriteImage.getHeight();

        int[] colourData = new int[width * height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int colour = ARGB.fromABGR(spriteImage.getPixel(x, y));
                int alpha = ARGB.alpha(colour);
                if (alpha == 0) {
                    continue;
                }

                int red = ARGB.red(colour);
                int green = ARGB.green(colour);
                int blue = ARGB.blue(colour);
                int packed = red << 16 | green << 8 | blue;
                colourData[x + y * width] = packed;
            }
        }

        return colourData;
    }
}
