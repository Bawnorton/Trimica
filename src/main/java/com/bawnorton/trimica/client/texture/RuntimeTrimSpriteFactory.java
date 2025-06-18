package com.bawnorton.trimica.client.texture;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

public interface RuntimeTrimSpriteFactory {
    TrimSpriteContents create(ResourceLocation texture, @Nullable ArmorTrim trim, @Nullable DataComponentGetter componentGetter);
}
