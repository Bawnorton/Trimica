package com.bawnorton.trimica.client.mixin.render;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.mixin.accessor.EquipmentLayerRenderer$TrimSpriteKeyAccessor;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.client.texture.DynamicTrimTextureAtlasSprite;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlas;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlases;
import com.bawnorton.trimica.client.texture.TrimArmourSpriteFactory;
import com.bawnorton.trimica.compat.Compat;
import com.bawnorton.trimica.item.component.AdditionalTrims;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

//? if >=1.21.10
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;

@MixinEnvironment("client")
@Mixin(EquipmentLayerRenderer.class)
abstract class EquipmentLayerRendererMixin {
	@Shadow
	@Final
	private Function<EquipmentLayerRenderer.TrimSpriteKey, TextureAtlasSprite> trimSpriteLookup;

	@WrapOperation(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/Util;memoize(Ljava/util/function/Function;)Ljava/util/function/Function;",
					ordinal = 1
			)
	)
	private Function<EquipmentLayerRenderer.TrimSpriteKey, TextureAtlasSprite> provideRuntimeTextures(Function<EquipmentLayerRenderer.TrimSpriteKey, TextureAtlasSprite> textureGetter, Operation<Function<EquipmentLayerRenderer.TrimSpriteKey, TextureAtlasSprite>> original) {
		return trimica$dynamicProvider(original.call(textureGetter));
	}

	@SuppressWarnings("resource")
	@Unique
	private static @NotNull Function<EquipmentLayerRenderer.TrimSpriteKey, TextureAtlasSprite> trimica$dynamicProvider(Function<EquipmentLayerRenderer.TrimSpriteKey, TextureAtlasSprite> textureGetter) {
		return trimSpriteKey -> {
			TextureAtlasSprite sprite = textureGetter.apply(trimSpriteKey);
			ProfilerFiller profiler = Profiler.get();
			profiler.push("trimica:armour_runtime_atlas");
			ItemStack stack = TrimArmourSpriteFactory.ITEM_WITH_TRIM_CAPTURE.get();
			MaterialAdditions addition = stack.getOrDefault(MaterialAdditions.TYPE, MaterialAdditions.NONE);
			if (!sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation()) && addition.isEmpty()) return sprite;

			RuntimeTrimAtlases atlases = TrimicaClient.getRuntimeAtlases();
			TrimMaterial material = trimSpriteKey.trim().material().value();
			RuntimeTrimAtlas atlas = atlases.getEquipmentAtlas(Minecraft.getInstance().level, material, trimSpriteKey.layerType());
			if (atlas == null) return sprite;

			ResourceLocation overlayLocation = trimSpriteKey.spriteId();
			overlayLocation = addition.apply(overlayLocation);
			TrimPattern pattern = trimSpriteKey.trim().pattern().value();
			DynamicTrimTextureAtlasSprite dynamicSprite = atlas.getSprite(stack, pattern, overlayLocation);
			profiler.pop();
			return dynamicSprite;
		};
	}

	//? if >=1.21.10 {
	@ModifyReceiver(
			method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/ResourceLocation;II)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"
			)
	)
	private <S> ItemStack captureItemWithTrimOrRenderAll(
			ItemStack instance,
			DataComponentType<?> dataComponentType,
			EquipmentClientInfo.LayerType layerType,
			ResourceKey<EquipmentAsset> equipmentAsset,
			Model<? super S> armorModel,
			S renderState,
			ItemStack item,
			PoseStack poseStack,
			SubmitNodeCollector nodeCollector,
			int packedLight,
			@Nullable ResourceLocation texture,
			int outlineColor,
			int key,
			@Cancellable CallbackInfo ci
	) {
		TrimArmourSpriteFactory.ITEM_WITH_TRIM_CAPTURE.set(instance);
		if (!AdditionalTrims.enableAdditionalTrims) {
			return instance;
		}

		ci.cancel();
		List<BiConsumer<Boolean, Boolean>> renderers = new ArrayList<>();
		boolean isEmissive = false;
		boolean isAnimated = false;
		List<ArmorTrim> trims = AdditionalTrims.getAllTrims(instance);
		for (ArmorTrim trim : trims) {
			TextureAtlasSprite sprite = trimSpriteLookup.apply(EquipmentLayerRenderer$TrimSpriteKeyAccessor.trimica$init(trim, layerType, equipmentAsset));
			TrimPalette palette;
			RenderType renderType;
			if (sprite instanceof DynamicTrimTextureAtlasSprite dynamicSprite) {
				palette = dynamicSprite.getPalette();
				if (palette != null) {
					if (palette.isAnimated()) isAnimated = true;
					if (palette.isEmissive()) isEmissive = true;
				}
				renderType = dynamicSprite.getRenderType();
			} else {
				renderType = Sheets.armorTrimsSheet(trim.pattern().value().decal());
			}
			AtomicInteger index = new AtomicInteger(key);
			renderers.add((emissive, animated) -> {
				if (animated) {
					Compat.ifSodiumPresent(compat -> compat.markSpriteAsActive(sprite));
				}
				nodeCollector.order(index.getAndIncrement())
						.submitModel(
								armorModel,
								renderState,
								poseStack,
								renderType,
								emissive ? LightTexture.FULL_BRIGHT : packedLight,
								OverlayTexture.NO_OVERLAY,
								-1,
								sprite,
								outlineColor,
								null
						);
			});
		}
		for (BiConsumer<Boolean, Boolean> renderer : renderers) {
			renderer.accept(isEmissive, isAnimated);
		}
		return instance;
	}

	@WrapOperation(
			method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/ResourceLocation;II)V",
			at = @At(
					value = "INVOKE:LAST",
					target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
			)
	)
	private <S> void useDynamicRenderType(
			OrderedSubmitNodeCollector instance,
			Model<? super S> model,
			S renderState,
			PoseStack poseStack,
			RenderType renderType,
			int packedLight,
			int packedOverlay,
			int tintColor,
			TextureAtlasSprite textureAtlasSprite,
			int outlineColor,
			ModelFeatureRenderer.CrumblingOverlay crumblingOverlay,
			Operation<Void> original
	) {
		if (textureAtlasSprite instanceof DynamicTrimTextureAtlasSprite dynamicSprite) {
			TrimPalette palette = dynamicSprite.getPalette();
			if (palette != null && palette.isAnimated()) {
				Compat.ifSodiumPresent(compat -> compat.markSpriteAsActive(dynamicSprite));
			}
			original.call(instance,
					model,
					renderState,
					poseStack,
					dynamicSprite.getRenderType(),
					palette != null ? (palette.isEmissive() ? LightTexture.FULL_BRIGHT : packedLight) : packedLight,
					packedOverlay,
					tintColor,
					textureAtlasSprite,
					outlineColor,
					crumblingOverlay
			);
		} else {
			original.call(instance,
					model,
					renderState,
					poseStack,
					renderType,
					packedLight,
					packedOverlay,
					tintColor,
					textureAtlasSprite,
					outlineColor,
					crumblingOverlay
			);
		}
	}

	//?} else {
	/*@ModifyReceiver(
			method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"
			)
	)
	private ItemStack captureItemWithTrimOrRenderAll(ItemStack instance, DataComponentType<?> dataComponentType,
	                                                 @Local(argsOnly = true) EquipmentClientInfo.LayerType layerType,
	                                                 @Local(argsOnly = true) ResourceKey<EquipmentAsset> equipmentAsset,
	                                                 @Local(argsOnly = true) Model armorModel,
	                                                 @Local(argsOnly = true) PoseStack poseStack,
	                                                 @Local(argsOnly = true) MultiBufferSource bufferSource,
	                                                 @Local(argsOnly = true) int packedLight,
	                                                 @Cancellable CallbackInfo ci) {
		TrimArmourSpriteFactory.ITEM_WITH_TRIM_CAPTURE.set(instance);
		if (!AdditionalTrims.enableAdditionalTrims) {
			return instance;
		}

		ci.cancel();
		List<BiConsumer<Boolean, Boolean>> renderers = new ArrayList<>();
		boolean isEmissive = false;
		boolean isAnimated = false;
		List<ArmorTrim> trims = AdditionalTrims.getAllTrims(instance);
		for (ArmorTrim trim : trims) {
			TextureAtlasSprite sprite = trimSpriteLookup.apply(EquipmentLayerRenderer$TrimSpriteKeyAccessor.trimica$init(trim, layerType, equipmentAsset));
			TrimPalette palette;
			RenderType renderType;
			if (sprite instanceof DynamicTrimTextureAtlasSprite dynamicSprite) {
				palette = dynamicSprite.getPalette();
				if (palette != null) {
					if (palette.isAnimated()) isAnimated = true;
					if (palette.isEmissive()) isEmissive = true;
				}
				renderType = dynamicSprite.getRenderType();
			} else {
				renderType = Sheets.armorTrimsSheet(trim.pattern().value().decal());
			}
			renderers.add((emissive, animated) -> {
				if (animated) {
					Compat.ifSodiumPresent(compat -> compat.markSpriteAsActive(sprite));
				}
				VertexConsumer vertexConsumer = sprite.wrap(bufferSource.getBuffer(renderType));
				armorModel.renderToBuffer(poseStack, vertexConsumer, emissive ? LightTexture.FULL_BRIGHT : packedLight, OverlayTexture.NO_OVERLAY);
			});
		}
		for (BiConsumer<Boolean, Boolean> renderer : renderers) {
			renderer.accept(isEmissive, isAnimated);
		}
		return instance;
	}

	@WrapOperation(
			method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
			)
	)
	private VertexConsumer useDynamicRenderType(MultiBufferSource instance, RenderType renderType, Operation<VertexConsumer> original, @Local TextureAtlasSprite textureAtlasSprite, @Share("palette") LocalRef<TrimPalette> paletteLocalRef) {
		if (textureAtlasSprite instanceof DynamicTrimTextureAtlasSprite dynamicSprite) {
			TrimPalette palette = dynamicSprite.getPalette();
			paletteLocalRef.set(palette);
			if (palette != null && palette.isAnimated()) {
				Compat.ifSodiumPresent(compat -> compat.markSpriteAsActive(dynamicSprite));
			}
			return original.call(instance, dynamicSprite.getRenderType());
		}
		return original.call(instance, renderType);
	}

	@WrapOperation(
			method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"
			)
	)
	private void usePaletteLightness(Model instance, PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, Operation<Void> original, @Share("palette") LocalRef<TrimPalette> paletteLocalRef) {
		TrimPalette palette = paletteLocalRef.get();
		int light = palette == null ? i : (palette.isEmissive() ? LightTexture.FULL_BRIGHT : i);
		original.call(instance, poseStack, vertexConsumer, light, j);
	}
	*///?}
}
