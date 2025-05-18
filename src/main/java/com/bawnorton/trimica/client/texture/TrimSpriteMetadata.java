package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.client.palette.TrimPalette;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.ArmorType;

public record TrimSpriteMetadata(TrimPalette palette, ResourceLocation baseTexture, ArmorType armorType) {
    public TrimSpriteMetadata(TrimPalette palette, ResourceLocation baseTexture) {
        this(palette, baseTexture, null);
    }

    public boolean isAnimated() {
        return palette.isAnimated();
    }
}
