package com.bawnorton.trimica.client.mixin;

import com.bawnorton.trimica.client.texture.DynamicTextureAtlasSprite;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlas;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlases;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.function.Function;

@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin {
    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/Util;memoize(Ljava/util/function/Function;)Ljava/util/function/Function;",
                    ordinal = 1
            )
    )
    private Function<EquipmentLayerRenderer.TrimSpriteKey, TextureAtlasSprite> provideRuntimeTextures(Function<EquipmentLayerRenderer.TrimSpriteKey, TextureAtlasSprite> textureGetter, Operation<Function<EquipmentLayerRenderer.TrimSpriteKey, TextureAtlasSprite>> original) {
        return original.call((Function<EquipmentLayerRenderer.TrimSpriteKey, TextureAtlasSprite>) trimSpriteKey -> {
            TextureAtlasSprite sprite = textureGetter.apply(trimSpriteKey);
            if (!sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) return sprite;

            RuntimeTrimAtlas atlas = RuntimeTrimAtlases.getModelAtlas(trimSpriteKey.trim());
            if (atlas == null) return sprite;

            return atlas.getSprite(trimSpriteKey.spriteId());
        });
    }

    @WrapOperation(
            method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            )
    )
    private VertexConsumer useDynamicRenderType(MultiBufferSource instance, RenderType renderType, Operation<VertexConsumer> original, @Local TextureAtlasSprite textureAtlasSprite) {
        if(textureAtlasSprite instanceof DynamicTextureAtlasSprite dynamicTextureAtlasSprite) {
            return original.call(instance, dynamicTextureAtlasSprite.getRenderType());
        }
        return original.call(instance, renderType);
    }
}
