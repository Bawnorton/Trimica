package com.bawnorton.trimica.client.mixin;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin {
    @Shadow public abstract Holder<TrimMaterial> material();

    @ModifyArg(
            method = "addToTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/MutableComponent;append(Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/MutableComponent;"
            )
    )
    private Component correctMaterialColour(Component original, @Local(argsOnly = true) DataComponentGetter componentGetter) {
        TrimPalette palette = TrimicaClient.getPalettes().getPalette(material().value(), null, componentGetter);
        if (palette == null) return original;

        if(palette.isAnimated()) {
            return original.copy().withColor(palette.getTooltipColour());
        } else {
            TextColor color = original.getStyle().getColor();
            if(color == null) {
                return original.copy().withColor(palette.getTooltipColour());
            }
        }
        return original;
    }
}
