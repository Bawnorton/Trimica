package com.bawnorton.trimica.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemDisplayContext;

//? if >=1.21.10
import net.minecraft.client.renderer.SubmitNodeCollector;

public interface TrimicaRenderer {
	//? if >=1.21.10 {
	void submitShieldTrim(ModelPart plate, DataComponentMap components, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor);
	//?} else {
	/*void renderShieldTrim(ModelPart plate, DataComponentMap dataComponentMap, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay, boolean hasFoil);
	*///?}
}
