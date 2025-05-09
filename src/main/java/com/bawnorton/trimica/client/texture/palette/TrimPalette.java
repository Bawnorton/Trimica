package com.bawnorton.trimica.client.texture.palette;

import com.bawnorton.trimica.client.texture.colour.ColourHSB;
import net.minecraft.Util;
import net.minecraft.util.ARGB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TrimPalette {
    public static final TrimPalette DEFAULT = new TrimPalette(ARGB.color(255, 255, 255, 255));
    public static final int PALETTE_SIZE = 8;
    private final List<Integer> colours;
    private final boolean builtin;

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

    public List<Integer> getColours() {
        return colours;
    }

    public boolean isBuiltin() {
        return builtin;
    }

    public int getTooltipColour() {
        List<ColourHSB> hsbColours = ColourHSB.fromRGB(colours);
        hsbColours.removeIf(colour -> colour.saturation() < 0.25f || colour.brightness() < 0.5f);
        if (hsbColours.isEmpty()) {
            return ARGB.toABGR(colours.getFirst());
        }
        Collections.sort(hsbColours);
        return ARGB.toABGR(hsbColours.getFirst().colour());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TrimPalette other) {
            return Objects.equals(colours, other.colours);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(colours);
    }
}
