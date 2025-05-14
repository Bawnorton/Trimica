package com.bawnorton.trimica.client.texture;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

public interface RuntimeTrimSpriteFactory {
    SpriteContents create(ResourceLocation texture, ArmorTrim trim, @Nullable DataComponentGetter componentGetter);
}
