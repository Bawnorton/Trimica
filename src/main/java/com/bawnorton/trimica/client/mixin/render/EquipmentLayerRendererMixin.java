package com.bawnorton.trimica.client.mixin.render;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.client.texture.DynamicTrimTextureAtlasSprite;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlas;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlases;
import com.bawnorton.trimica.client.texture.TrimArmourSpriteFactory;
import com.bawnorton.trimica.compat.Compat;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.function.Function;

@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin {
    @ModifyReceiver(
            method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"
            )
    )
    private ItemStack captureItemWithTrim(ItemStack instance, DataComponentType<?> dataComponentType) {
        TrimArmourSpriteFactory.ITEM_WITH_TRIM_CAPTURE.set(instance);
        return instance;
    }

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
            TrimMaterial material = trimSpriteKey.trim().material().value();
            ItemStack stack = TrimArmourSpriteFactory.ITEM_WITH_TRIM_CAPTURE.get();
            MaterialAdditions addition = stack.get(MaterialAdditions.TYPE);
            if (!sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation()) && addition == null) return sprite;

            RuntimeTrimAtlases atlases = TrimicaClient.getRuntimeAtlases();
            TrimPattern pattern = trimSpriteKey.trim().pattern().value();
            RuntimeTrimAtlas atlas = atlases.getEquipmentAtlas(pattern, trimSpriteKey.layerType());
            if (atlas == null) return sprite;

            ResourceLocation overlayLocation = trimSpriteKey.spriteId();
            if (addition != null) {
                overlayLocation = addition.apply(overlayLocation);
            }
            DynamicTrimTextureAtlasSprite dynamicSprite = atlas.getSprite(stack, material, overlayLocation);
            profiler.pop();
            return dynamicSprite;
        };
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
            if(palette != null && palette.isAnimated()) {
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
}
