package com.bawnorton.trimica.api.client.impl;

import com.bawnorton.trimica.api.client.TrimicaRenderer;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.client.texture.DynamicTrimTextureAtlasSprite;
import com.bawnorton.trimica.compat.Compat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.List;

//? if >=1.21.10
import net.minecraft.client.renderer.SubmitNodeCollector;

public final class TrimicaRendererImpl implements TrimicaRenderer {
	//? if >=1.21.10 {
	public void submitShieldTrim(ModelPart plate, DataComponentMap components, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
		ProfilerFiller profiler = Profiler.get();
		profiler.push("trimica:shield");
		List<DynamicTrimTextureAtlasSprite> dynamicSprites = TrimicaClient.getRuntimeAtlases().getShieldSprites(Minecraft.getInstance().level, components);
		for (DynamicTrimTextureAtlasSprite dynamicSprite : dynamicSprites) {
			TrimPalette palette = dynamicSprite.getPalette();
			int light = palette == null ? packedLight : (palette.isEmissive() ? LightTexture.FULL_BRIGHT : packedLight);
			if (palette != null && palette.isAnimated()) {
				Compat.ifSodiumPresent(compat -> compat.markSpriteAsActive(dynamicSprite));
			}
			nodeCollector.submitModelPart(
					plate,
					poseStack,
					dynamicSprite.getRenderType(),
					light,
					packedOverlay,
					dynamicSprite,
					false,
					hasFoil,
					-1,
					null,
					outlineColor
			);
		}
		profiler.pop();
	}
	//?} else {
	/*public void renderShieldTrim(ModelPart plate, DataComponentMap dataComponentMap, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay, boolean hasFoil) {
		ProfilerFiller profiler = Profiler.get();
		profiler.push("trimica:shield");
		List<DynamicTrimTextureAtlasSprite> dynamicSprites = TrimicaClient.getRuntimeAtlases().getShieldSprites(Minecraft.getInstance().level, dataComponentMap);
		for (DynamicTrimTextureAtlasSprite dynamicSprite : dynamicSprites) {
			TrimPalette palette = dynamicSprite.getPalette();
			int light = palette == null ? packedLight : (palette.isEmissive() ? LightTexture.FULL_BRIGHT : packedLight);
			if (palette != null && palette.isAnimated()) {
				Compat.ifSodiumPresent(compat -> compat.markSpriteAsActive(dynamicSprite));
			}
			VertexConsumer vertexConsumer = dynamicSprite.wrap(ItemRenderer.getFoilBuffer(multiBufferSource, dynamicSprite.getRenderType(), itemDisplayContext == ItemDisplayContext.GUI, hasFoil));
			plate.render(poseStack, vertexConsumer, light, packedOverlay);
		}

		profiler.pop();
	}
	*///?}
}
