package com.bawnorton.trimica.client.mixin;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.AnimatedTrimPalette;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(
            method = "setLevel",
            at = @At("TAIL")
    )
    private void initTrimAtlases(ClientLevel clientLevel, ReceivingLevelScreen.Reason reason, CallbackInfo ci) {
        TrimicaClient.getRuntimeAtlases().init(clientLevel.registryAccess());
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void tickAnimations(CallbackInfo ci) {
        AnimatedTrimPalette.computeColours();
    }
}
