package com.bawnorton.trimica.client.mixin;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.AnimatedTrimPalette;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <1.21.10 {
/*import net.minecraft.client.gui.screens.ReceivingLevelScreen;
*///?}

@MixinEnvironment(value = "client")
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Inject(
			method = "setLevel",
			at = @At("TAIL")
	)
	//? if >=1.21.10 {
	private void initTrimAtlases(ClientLevel clientLevel, CallbackInfo ci) {
	//?} else {
	/*private void initTrimAtlases(ClientLevel clientLevel, ReceivingLevelScreen.Reason reason, CallbackInfo ci) {
	*///?}
		TrimicaClient.getRuntimeAtlases().init(clientLevel.registryAccess());
	}

	@Inject(
			method = "tick",
			at = @At("TAIL")
	)
	private void tickAnimations(CallbackInfo ci) {
		AnimatedTrimPalette.updateOffset();
	}
}
