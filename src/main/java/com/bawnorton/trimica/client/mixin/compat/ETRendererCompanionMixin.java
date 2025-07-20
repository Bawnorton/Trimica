package com.bawnorton.trimica.client.mixin.compat;

import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.client.texture.DynamicTrimTextureAtlasSprite;
import com.bawnorton.trimica.compat.Compat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.kikugie.elytratrims.render.ETRenderer;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@MixinEnvironment(value = "client")
@Mixin(ETRenderer.Companion.class)
public abstract class ETRendererCompanionMixin {
    @WrapOperation(
            method = "*(Lnet/minecraft/client/model/Model;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/item/ItemStack;IILnet/minecraft/resources/ResourceLocation;)V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/renderer/entity/ItemRenderer.getArmorFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;Z)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            )
    )
    private VertexConsumer useDynamicRenderType(MultiBufferSource instance, RenderType renderType, boolean hasFoil, Operation<VertexConsumer> original, @Local(argsOnly = true) TextureAtlasSprite textureAtlasSprite, @Share("palette") LocalRef<TrimPalette> paletteLocalRef) {
        if (textureAtlasSprite instanceof DynamicTrimTextureAtlasSprite dynamicSprite) {
            TrimPalette palette = dynamicSprite.getPalette();
            paletteLocalRef.set(palette);
            if(palette != null && palette.isAnimated()) {
                Compat.ifSodiumPresent(compat -> compat.markSpriteAsActive(dynamicSprite));
            }
            return original.call(instance, dynamicSprite.getRenderType(), hasFoil);
        }
        return original.call(instance, renderType, hasFoil);
    }

    @WrapOperation(
            method = "*(Lnet/minecraft/client/model/Model;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/item/ItemStack;IILnet/minecraft/resources/ResourceLocation;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"
            )
    )
    private void usePaletteLightness(Model instance, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color, Operation<Void> original, @Share("palette") LocalRef<TrimPalette> paletteLocalRef) {
        TrimPalette palette = paletteLocalRef.get();
        int light = palette == null ? packedLight : (palette.isEmissive() ? LightTexture.FULL_BRIGHT : packedLight);
        original.call(instance, poseStack, vertexConsumer, light, packedOverlay, color);
    }
}
