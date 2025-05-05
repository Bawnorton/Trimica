package com.bawnorton.trimica.client.texture;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.trim.ArmorTrim;

public class TrimItemFactory implements RuntimeTrimSpriteFactory {
    @Override
    public DynamicTextureAtlasSprite apply(ArmorTrim trim, ResourceLocation location) {
        return null;
    }
}
