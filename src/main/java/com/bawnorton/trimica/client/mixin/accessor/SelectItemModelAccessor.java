package com.bawnorton.trimica.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.item.SelectItemModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@MixinEnvironment(value = "client")
@Mixin(SelectItemModel.class)
public interface SelectItemModelAccessor {
    @Accessor("models")
    SelectItemModel.ModelSelector<?> trimica$models();
}
