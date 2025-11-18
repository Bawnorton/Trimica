package com.bawnorton.trimica.client.palette;

import com.bawnorton.trimica.client.colour.ColourHSB;
import net.minecraft.Util;
import net.minecraft.util.ARGB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TrimPalette {
	public static final TrimPalette DEFAULT = new TrimPalette(ARGB.color(0xFF, 0xFF, 0xFF, 0xFF));
	public static final TrimPalette MISSING = new TrimPalette(List.of(
			ARGB.color(0xFF, 0xFF, 0, 0xFF),
			ARGB.color(0xFF, 0, 0, 0),
			ARGB.color(0xFF, 0xFF, 0, 0xFF),
			ARGB.color(0xFF, 0, 0, 0),
			ARGB.color(0xFF, 0xFF, 0, 0xFF),
			ARGB.color(0xFF, 0, 0, 0),
			ARGB.color(0xFF, 0xFF, 0, 0xFF),
			ARGB.color(0xFF, 0, 0, 0)
	));
	public static final TrimPalette DISABLED = new TrimPalette(List.of(
			ARGB.color(0xFF, 0xFF, 0, 0),
			ARGB.color(0xFF, 0, 0, 0),
			ARGB.color(0xFF, 0xFF, 0, 0),
			ARGB.color(0xFF, 0, 0, 0),
			ARGB.color(0xFF, 0xFF, 0, 0),
			ARGB.color(0xFF, 0, 0, 0),
			ARGB.color(0xFF, 0xFF, 0, 0),
			ARGB.color(0xFF, 0, 0, 0)
	));

	public static final int PALETTE_SIZE = 8;
	private final List<Integer> colours;
	private final boolean builtin;
	private boolean emissive = false;

	public TrimPalette(List<Integer> colours, boolean builtin) {
		if (colours.size() != PALETTE_SIZE) {
			throw new IllegalArgumentException("Trim palette requires exactly %s colours, but %s were found.".formatted(PALETTE_SIZE, colours.size()));
		}
		this.colours = new ArrayList<>(colours);
		this.builtin = builtin;
	}

	public TrimPalette(List<Integer> colours) {
		this(colours, false);
	}

	public TrimPalette(int singleColour) {
		this(Util.make(new ArrayList<>(), colours -> {
			for (int i = 0; i < PALETTE_SIZE; i++) {
				colours.add(singleColour);
			}
		}));
	}

	public TrimPalette asAnimated() {
		AnimatedTrimPalette animatedTrimPalette = new AnimatedTrimPalette(colours);
		animatedTrimPalette.setEmissive(emissive);
		return animatedTrimPalette;
	}

	public boolean isAnimated() {
		return false;
	}

	public void setEmissive(boolean emissive) {
		this.emissive = emissive;
	}

	public boolean isEmissive() {
		return emissive;
	}

	public List<Integer> getColours() {
		return colours;
	}

	public boolean isBuiltin() {
		return builtin;
	}

	public int getTooltipColour() {
		List<ColourHSB> hsbColours = ColourHSB.fromARGB(colours);
		hsbColours.removeIf(colour -> colour.saturation() < 0.25f || colour.brightness() < 0.5f);
		if (hsbColours.isEmpty()) {
			return colours.getFirst();
		}
		Collections.sort(hsbColours);
		return hsbColours.getFirst().rgb();
	}

	public String getMetadataString() {
		StringBuilder metadata = new StringBuilder();
		if (isBuiltin()) {
			metadata.append("built-in_");
		}
		if (isEmissive()) {
			metadata.append("emissive_");
		}
		if (isAnimated()) {
			metadata.append("animated_");
		}
		if (metadata.isEmpty()) {
			metadata.append("default_");
		}
		metadata.deleteCharAt(metadata.length() - 1);
		return metadata.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TrimPalette other) {
			return Objects.equals(colours, other.colours);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(colours);
	}
}