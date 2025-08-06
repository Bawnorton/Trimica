package com.bawnorton.trimica.mixin;

import com.bawnorton.trimica.TrimicaToggles;
import com.bawnorton.trimica.item.TrimicaItems;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment
@Mixin(CreativeModeTabs.class)
public abstract class CreativeModeTabsMixin {
    @Inject(
            //? if fabric {
            method = "method_51321",
            //?} else {
            /*method = "lambda$bootstrap$26",
            *///?}
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/item/Items;SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE:Lnet/minecraft/world/item/Item;"
            )
    )
    private static void addTrimicaItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output, CallbackInfo ci) {
        if(TrimicaToggles.enableItems) {
            if(TrimicaToggles.enableRainbowifier) {
                output.accept(TrimicaItems.RAINBOWIFIER);
            }
            if(TrimicaToggles.enableAnimator) {
                output.accept(TrimicaItems.ANIMATOR);
            }
        }
    }
}
