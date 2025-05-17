package com.bawnorton.trimica.mixin.component;

import com.bawnorton.trimica.Trimica;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ProvidesTrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataComponentHolder.class)
public interface DataComponentHolderMixin {
    @SuppressWarnings({"ConstantValue", "unchecked"})
    @ModifyReturnValue(
            method = "get",
            at = @At("RETURN")
    )
    default <T> T createRuntimeTrimMaterialProviderIfMissing(T original, DataComponentType<? extends T> type) {
        if (type == DataComponents.PROVIDES_TRIM_MATERIAL && original == null && (Object) this instanceof ItemStack stack) {
            return (T) new ProvidesTrimMaterial(Holder.direct(Trimica.getMaterialRegistry().getOrCreate(stack)));
        }
        return original;
    }
}
