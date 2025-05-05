package com.bawnorton.trimica.client.mixin.accessor;

import net.minecraft.client.renderer.item.SelectItemModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SelectItemModel.class)
public interface SelectItemModelAccessor {
    @Accessor("models")
    SelectItemModel.ModelSelector<?> trimica$models();
}
