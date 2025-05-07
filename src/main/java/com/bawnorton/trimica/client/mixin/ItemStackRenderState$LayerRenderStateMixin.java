package com.bawnorton.trimica.client.mixin;

import com.bawnorton.trimica.client.extend.ItemStackRenderState$LayerRenderStateExtender;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public abstract class ItemStackRenderState$LayerRenderStateMixin implements ItemStackRenderState$LayerRenderStateExtender {
    @Unique
    private boolean trimica$isTrimOverlayLayer = false;

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderItem(Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II[ILjava/util/List;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/item/ItemStackRenderState$FoilType;)V"
            )
    )
    private void moveOverlayLayerForward(ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, int[] ints, List<BakedQuad> list, RenderType renderType, ItemStackRenderState.FoilType foilType, Operation<Void> original) {
        if (trimica$isTrimOverlayLayer) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 0.0001);
            original.call(itemDisplayContext, poseStack, multiBufferSource, i, j, ints, list, renderType, foilType);
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
}
