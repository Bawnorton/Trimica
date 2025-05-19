package com.bawnorton.trimica.mixin.component;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.item.component.MaterialAddition;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ProvidesTrimMaterial;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataComponentHolder.class)
public interface DataComponentHolderMixin {
    @Shadow @Nullable <T> T get(DataComponentType<? extends T> dataComponentType);

    @SuppressWarnings({"ConstantValue", "unchecked"})
    @ModifyReturnValue(
            method = "get",
            at = @At("RETURN")
    )
    default <T> T fakeComponents(T original, DataComponentType<? extends T> type) {
        if (type == DataComponents.PROVIDES_TRIM_MATERIAL && original == null && (Object) this instanceof ItemStack stack) {
            return (T) new ProvidesTrimMaterial(Holder.direct(Trimica.getMaterialRegistry().getOrCreate(stack)));
        } else if (type == MaterialAddition.TYPE && original == null) {
            ArmorTrim trim = get(DataComponents.TRIM);
            if(trim == null) return original;

            TrimMaterial trimMaterial = trim.material().value();
            if(Trimica.getMaterialRegistry().getIsAnimated(trimMaterial)) {
                return (T) new MaterialAddition(BuiltInRegistries.ITEM.getKey(TrimicaItems.ANIMATOR_MATERIAL));
            }
        }
        return original;
    }
}
