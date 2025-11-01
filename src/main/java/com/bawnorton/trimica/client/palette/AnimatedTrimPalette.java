package com.bawnorton.trimica.client.palette;

import com.bawnorton.trimica.client.colour.OkLabHelper;
import net.minecraft.util.ARGB;

import java.util.ArrayList;
import java.util.List;

public final class AnimatedTrimPalette extends TrimPalette {
	public static final int ANIMATED_PALETTE_SIZE = 9 * PALETTE_SIZE;

	private static int offset = 0;

	private final List<Integer> interpolatedColours;

	AnimatedTrimPalette(List<Integer> colours) {
		super(colours);
		int first = colours.getFirst();
		List<Integer> base = new ArrayList<>(PALETTE_SIZE + 1);
		base.addAll(colours);
		base.add(first);
		List<double[]> okLab = OkLabHelper.rgbToOklab(base);
		List<double[]> stretched = OkLabHelper.strechOkLab(ANIMATED_PALETTE_SIZE, PALETTE_SIZE + 1, okLab);
		interpolatedColours = OkLabHelper.okLabToRgb(stretched);
	}

	@Override
	public TrimPalette asAnimated() {
		return this;
	}

	@Override
	public boolean isAnimated() {
		return true;
	}

	public static void updateOffset() {
		offset = (offset + 1) % ANIMATED_PALETTE_SIZE;
	}

	@Override
	public int getTooltipColour() {
		return ARGB.toABGR(interpolatedColours.get(offset));
	}

	public List<Integer> getAnimationColours() {
		List<Integer> colours = new ArrayList<>(ANIMATED_PALETTE_SIZE / 3);
		for (int i = 0; i < ANIMATED_PALETTE_SIZE; i += 3) {
			colours.add(interpolatedColours.get(i % ANIMATED_PALETTE_SIZE));
		}
		return colours;
	}
}
