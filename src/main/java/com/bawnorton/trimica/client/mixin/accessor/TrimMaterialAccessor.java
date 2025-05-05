package com.bawnorton.trimica.client.mixin.accessor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrimMaterial.class)
public interface TrimMaterialAccessor {
    @Accessor("description")
    @Mutable
    void trimica$description(Component description);
}
