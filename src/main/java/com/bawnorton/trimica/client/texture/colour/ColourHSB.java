package com.bawnorton.trimica.client.texture.colour;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

public record ColourHSB(Integer colour, float saturation, float brightness) {
    public static ColourHSB fromRGB(int rgb) {
        int red = rgb >> 16 & 255;
        int green = rgb >> 8 & 255;
        int blue = rgb & 255;

        float[] hsbValues = Color.RGBtoHSB(red, green, blue, null);
        return new ColourHSB(rgb, hsbValues[0], hsbValues[1]);
    }

    public static List<ColourHSB> fromRGB(List<Integer> colours) {
        return colours.stream().map(ColourHSB::fromRGB).collect(Collectors.toList());
    }
}