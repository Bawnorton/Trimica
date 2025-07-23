package com.bawnorton.trimica.client.mixin.render;

import com.bawnorton.trimica.client.extend.ItemStackRenderState$LayerRenderStateExtender;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.compat.Compat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@MixinEnvironment(value = "client")
@Mixin(ItemStackRenderState.LayerRenderState.class)
public abstract class ItemStackRenderState$LayerRenderStateMixin implements ItemStackRenderState$LayerRenderStateExtender {
    @Unique
    private boolean trimica$isTrimOverlayLayer = false;

    @Unique
    private boolean trimica$emissive = false;

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderItem(Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II[ILjava/util/List;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/item/ItemStackRenderState$FoilType;)V"
            )
    )
    private void scaleOverlay(ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, int[] ints, List<BakedQuad> list, RenderType renderType, ItemStackRenderState.FoilType foilType, Operation<Void> original) {
        if (trimica$isTrimOverlayLayer) {
            poseStack.pushPose();
            PoseStack.Pose lastPose = poseStack.last();
            Matrix4f pose = lastPose.pose();
            Vector3f translation = pose.getTranslation(new Vector3f());
            pose.setTranslation(0, 0, 0);
            float margin = 0.001f;
            float scale = 1 + margin;
            pose.scale(scale, scale, scale + 0.05f);
            pose.setTranslation(translation);
            pose.translate(-margin / 2, -margin / 2, -0.0245f);
            int light = trimica$emissive ? LightTexture.FULL_BRIGHT : i;
            original.call(itemDisplayContext, poseStack, multiBufferSource, light, j, ints, list, renderType, foilType);
            poseStack.popPose();
            trimica$isTrimOverlayLayer = false;
        } else {
            original.call(itemDisplayContext, poseStack, multiBufferSource, i, j, ints, list, renderType, foilType);
        }
    }

    @Override
    public void trimica$markAsTrimOverlay() {
        trimica$isTrimOverlayLayer = true;
    }

    @Override
    public void trimica$setEmissive(boolean emissive) {
        this.trimica$emissive = emissive;
    }
}
