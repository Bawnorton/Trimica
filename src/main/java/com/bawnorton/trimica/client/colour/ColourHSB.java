package com.bawnorton.trimica.client.colour;

import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

public record ColourHSB(Integer colour, float hue, float saturation, float brightness) implements Comparable<ColourHSB> {
    public static ColourHSB fromRGB(int rgb) {
        int red = rgb >> 16 & 255;
        int green = rgb >> 8 & 255;
        int blue = rgb & 255;

        float[] hsbValues = Color.RGBtoHSB(red, green, blue, null);
        return new ColourHSB(rgb, hsbValues[0], hsbValues[1], hsbValues[2]);
    }

    public static List<ColourHSB> fromRGB(List<Integer> colours) {
        return colours.stream().map(ColourHSB::fromRGB).collect(Collectors.toList());
    }

    @Override
    public @NotNull String toString() {
        return "ColourHSB[%s, (%.2f, %.2f, %.2f)]".formatted(
                String.format("#%06X", colour),
                hue,
                saturation,
                brightness
        );
    }

    @Override
    public int compareTo(@NotNull ColourHSB o) {
        if (this.hue != o.hue) {
            return Float.compare(this.hue, o.hue);
        }
        if (this.saturation != o.saturation) {
            return Float.compare(this.saturation, o.saturation);
        }
        return Float.compare(this.brightness, o.brightness);
    }
}