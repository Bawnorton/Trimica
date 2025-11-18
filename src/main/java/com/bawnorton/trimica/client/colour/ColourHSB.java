package com.bawnorton.trimica.client.colour;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

public record ColourHSB(Integer rgb, float hue, float saturation, float brightness) implements Comparable<ColourHSB> {
	public static ColourHSB fromARGB(int argb) {
		int rgb = argb & 0xFFFFFF;
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;

		float[] hsbValues = Color.RGBtoHSB(red, green, blue, null);
		return new ColourHSB(rgb, hsbValues[0], hsbValues[1], hsbValues[2]);
	}

	public static List<ColourHSB> fromARGB(List<Integer> colours) {
		return colours.stream().map(ColourHSB::fromARGB).collect(Collectors.toList());
	}

	@Override
	public @NotNull String toString() {
		return "ColourHSB[%s, (%.2f, %.2f, %.2f)]".formatted(
				String.format("#%06X", rgb),
				hue,
				saturation,
				brightness
		);
	}

	@Override
	public int compareTo(@NotNull ColourHSB o) {
		if (this.hue != o.hue) {
			return Float.compare(o.hue, this.hue);
		}
		if (this.saturation != o.saturation) {
			return Float.compare(o.saturation, this.saturation);
		}
		return Float.compare(o.brightness, this.brightness);
	}
}