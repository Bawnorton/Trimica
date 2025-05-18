package com.bawnorton.trimica.client.mixin;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.mixin.accessor.TrimMaterialAccessor;
import com.bawnorton.trimica.client.palette.AnimatedTrimPalette;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Optional;

@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin {
    @Shadow public abstract Holder<TrimMaterial> material();

    @Inject(
            method = "addToTooltip",
            at = @At("HEAD")
    )
    private void correctMaterialColour(CallbackInfo ci, @Local(argsOnly = true) DataComponentGetter componentGetter) {
        ResourceKey<EquipmentAsset> equipmentAssetKey = Optional.ofNullable(componentGetter.get(DataComponents.EQUIPPABLE))
                .flatMap(Equippable::assetId)
                .orElse(null);
        TrimPalette palette = TrimicaClient.getPalettes().getPalette(material().value(), equipmentAssetKey);
        if (palette == null) return;

        Component oldDescription = material().value().description();
        if(palette.isAnimated()) {
            ((TrimMaterialAccessor) (Object) material().value()).trimica$description(oldDescription.copy().withColor(palette.getTooltipColour()));
        } else {
            TextColor color = oldDescription.getStyle().getColor();
            if(color == null) {
                ((TrimMaterialAccessor) (Object) material().value()).trimica$description(oldDescription.copy().withColor(palette.getTooltipColour()));
            }
        }
    }
}
