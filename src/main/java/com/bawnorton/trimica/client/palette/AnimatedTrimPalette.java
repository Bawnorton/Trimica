package com.bawnorton.trimica.client.palette;

import com.bawnorton.trimica.client.colour.OkLabHelper;
import net.minecraft.util.ARGB;
import java.util.ArrayList;
import java.util.List;

public final class AnimatedTrimPalette extends TrimPalette {
    private static final List<AnimatedTrimPalette> ANIMATED_PALETTES = new ArrayList<>();
    public static final int ANIMATED_PALETTE_SIZE = 9 * PALETTE_SIZE;

    private final List<Integer> interpolatedColours;
    private int offset = 0;

    AnimatedTrimPalette(List<Integer> colours) {
        super(colours);
        int first = colours.getFirst();
        List<Integer> base = new ArrayList<>(PALETTE_SIZE + 1);
        base.addAll(colours);
        base.add(first);
        List<double[]> okLab = OkLabHelper.rgbToOklab(base);
        List<double[]> stretched = OkLabHelper.strechOkLab(ANIMATED_PALETTE_SIZE, PALETTE_SIZE + 1, okLab);
        interpolatedColours = OkLabHelper.okLabToRgb(stretched);
        ANIMATED_PALETTES.add(this);
    }

    @Override
    public AnimatedTrimPalette asAnimated() {
        return this;
    }

    @Override
    public boolean isAnimated() {
        return true;
    }

    public static void computeColours() {
        for (AnimatedTrimPalette palette : ANIMATED_PALETTES) {
            palette.updateColours();
        }
    }

    private void updateColours() {
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
