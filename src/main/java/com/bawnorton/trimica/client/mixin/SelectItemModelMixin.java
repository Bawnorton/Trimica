package com.bawnorton.trimica.client.mixin;

import com.bawnorton.trimica.client.TrimicaClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SelectItemModel.class)
public abstract class SelectItemModelMixin<T> {
    @Shadow @Final private SelectItemModelProperty<T> property;

    @SuppressWarnings("MixinExtrasOperationParameters")
    @WrapOperation(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/SelectItemModel$ModelSelector;get(Ljava/lang/Object;Lnet/minecraft/client/multiplayer/ClientLevel;)Lnet/minecraft/client/renderer/item/ItemModel;"
            )
    )
    private ItemModel provideRuntimeModel(SelectItemModel.ModelSelector<T> instance, T t, ClientLevel clientLevel, Operation<ItemModel> original, @Local(argsOnly = true) ItemStack stack) {
        if(property instanceof TrimMaterialProperty && stack.has(DataComponents.TRIM)) {
            ItemModel base = original.call(instance, null, clientLevel);
            ArmorTrim trim = stack.get(DataComponents.TRIM);
            return TrimicaClient.getItemModelFactory().getOrCreateModel(base, stack, trim);
        }
        return original.call(instance, t, clientLevel);
    }
}
