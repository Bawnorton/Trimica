package com.bawnorton.trimica.client.mixin.render;

import com.bawnorton.trimica.api.client.TrimicaClientApi;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.21.10
import net.minecraft.client.renderer.SubmitNodeCollector;

@MixinEnvironment("client")
@Mixin(ShieldSpecialRenderer.class)
abstract class ShieldSpecialRendererMixin {
	@Shadow
	@Final
	private ShieldModel model;

	//? if >=1.21.10 {
	@Inject(
			method = "submit(Lnet/minecraft/core/component/DataComponentMap;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIZI)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
			)
	)
	private void submitTrim(DataComponentMap argument, ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor, CallbackInfo ci) {
		TrimicaClientApi.getInstance().getRenderer().submitShieldTrim(model.plate(), argument, poseStack, nodeCollector, packedLight, packedOverlay, hasFoil, outlineColor);
	}
	//?} else {
	/*@Inject(
			method = "render(Lnet/minecraft/core/component/DataComponentMap;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIZ)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
			)
	)
	private void renderTrim(DataComponentMap dataComponentMap, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay, boolean hasFoild, CallbackInfo ci) {
		TrimicaClientApi.getInstance().getRenderer().renderShieldTrim(model.plate(), dataComponentMap, itemDisplayContext, poseStack, multiBufferSource, packedLight, packedOverlay, hasFoild);
	}
	*///?}
}
