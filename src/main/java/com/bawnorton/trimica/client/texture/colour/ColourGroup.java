package com.bawnorton.trimica.client.texture.colour;

import java.util.ArrayList;
import java.util.List;

public final class ColourGroup {
    private final ColourHSB representative;
    private final List<ColourHSB> colours;

    public ColourGroup(ColourHSB representative) {
        this.representative = representative;
        this.colours = new ArrayList<>();
        this.colours.add(representative);
    }

    public ColourHSB getRepresentative() {
        return representative;
    }

    public void addMember(ColourHSB colour) {
        this.colours.add(colour);
    }

    public int getWeight() {
        return colours.size();
    }

    public boolean isSimilar(ColourHSB colour) {
        boolean saturationSimilar = Math.abs(colour.saturation() - representative.saturation()) < 0.1f;
        boolean brightnessSimilar = Math.abs(colour.brightness() - representative.brightness()) < 0.1f;
        boolean hueSimilar = Math.abs(colour.hue() - representative.hue()) < 0.1f;
        return saturationSimilar && brightnessSimilar && hueSimilar;
    }

    @Override
    public String toString() {
        return "ColourGroup[%s, (%d)]".formatted(representative, colours.size());
    }
}
