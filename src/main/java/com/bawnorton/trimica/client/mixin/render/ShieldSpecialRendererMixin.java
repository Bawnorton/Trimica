package com.bawnorton.trimica.client.mixin.render;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.client.texture.DynamicTrimTextureAtlasSprite;
import com.bawnorton.trimica.compat.Compat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment(value = "client")
@Mixin(ShieldSpecialRenderer.class)
public abstract class ShieldSpecialRendererMixin {
    @Shadow @Final private ShieldModel model;

    @Inject(
            method = "render(Lnet/minecraft/core/component/DataComponentMap;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
            )
    )
    private void renderTrim(DataComponentMap dataComponentMap, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, boolean bl, CallbackInfo ci) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("trimica:shield");
        DynamicTrimTextureAtlasSprite dynamicSprite = TrimicaClient.getRuntimeAtlases().getShieldSprite(dataComponentMap);
        if (dynamicSprite == null) return;

        TrimPalette palette = dynamicSprite.getPalette();
        int light = palette == null ? i : (palette.isEmissive() ? LightTexture.FULL_BRIGHT : i);
        if(palette != null && palette.isAnimated()) {
            Compat.ifSodiumPresent(compat -> compat.markSpriteAsActive(dynamicSprite));
        }
        VertexConsumer vertexConsumer = dynamicSprite.wrap(ItemRenderer.getFoilBuffer(multiBufferSource, dynamicSprite.getRenderType(), itemDisplayContext == ItemDisplayContext.GUI, bl));
        this.model.plate().render(poseStack, vertexConsumer, light, j);
        profiler.pop();
    }
}
