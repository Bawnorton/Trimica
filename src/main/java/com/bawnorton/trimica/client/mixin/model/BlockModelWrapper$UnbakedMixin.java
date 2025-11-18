package com.bawnorton.trimica.client.mixin.model;

import com.bawnorton.trimica.client.TrimicaClient;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@MixinEnvironment("client")
@Mixin(BlockModelWrapper.Unbaked.class)
abstract class BlockModelWrapper$UnbakedMixin {
	@Shadow
	@Final
	private ResourceLocation model;

	@ModifyReturnValue(
			method = "bake",
			at = @At("RETURN")
	)
	private ItemModel captureBakedModel(ItemModel original) {
		TrimicaClient.getItemModelFactory().registerBakedModel(original, model);
		return original;
	}
}
