package com.bawnorton.trimica.client.mixin;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.mixin.accessor.TrimMaterialAccessor;
import com.bawnorton.trimica.client.texture.palette.TrimPalette;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin {
    @Shadow public abstract Holder<TrimMaterial> material();

    @Inject(
            method = "addToTooltip",
            at = @At("HEAD")
    )
    private void correctMaterialColour(CallbackInfo ci) {
        TrimPalette palette = TrimicaClient.getPalettes().getPalette(material().value());
        if (palette == null) return;

        Component newDescription = material().value().description().copy().withColor(palette.getTooltipColour());
        ((TrimMaterialAccessor) (Object) material().value()).trimica$description(newDescription);
    }
}
