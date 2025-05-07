package com.bawnorton.trimica.client.mixin;

import com.bawnorton.trimica.client.extend.ItemStackRenderState$LayerRenderStateExtender;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import java.util.ArrayList;
import java.util.List;

@Mixin(BlockModelWrapper.class)
public abstract class BlockModelWrapperMixin {
    @Shadow @Final @Mutable
    private List<BakedQuad> quads;

    @Shadow
    private static boolean hasSpecialAnimatedTexture(ItemStack stack) {
        throw new AssertionError();
    }

    @Shadow @Final private ModelRenderProperties properties;

    @WrapMethod(
            method = "update"
    )
    private void createNewLayerRenderStateForTrimOverlay(ItemStackRenderState itemStackRenderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext itemDisplayContext, ClientLevel clientLevel, LivingEntity livingEntity, int i, Operation<Void> original) {
        Runnable callOriginal = () -> original.call(itemStackRenderState, stack, itemModelResolver, itemDisplayContext, clientLevel, livingEntity, i);
        if (!stack.has(DataComponents.TRIM)) {
            callOriginal.run();
            return;
        }

        int lastBaseTextureIndex = -1;
        ResourceLocation overlayAtlas = null;
        for (int j = 0; j < quads.size(); j++) {
            BakedQuad quad = quads.get(j);
            if (!quad.sprite().atlasLocation().equals(TextureAtlas.LOCATION_BLOCKS)) {
                lastBaseTextureIndex = j;
                overlayAtlas = quad.sprite().atlasLocation();
                break;
            }
        }
        if (overlayAtlas == null) {
            callOriginal.run();
            return;
        }

        List<BakedQuad> originalQuads = new ArrayList<>(quads);
        List<BakedQuad> overlayQuads = new ArrayList<>(quads.subList(lastBaseTextureIndex, quads.size()));
        quads = new ArrayList<>(quads.subList(0, lastBaseTextureIndex));
        callOriginal.run();

        ItemStackRenderState.LayerRenderState overlayRenderState = itemStackRenderState.newLayer();
        ((ItemStackRenderState$LayerRenderStateExtender) overlayRenderState).trimica$markAsTrimOverlay();
        if(stack.hasFoil()) {
            overlayRenderState.setFoilType(ItemStackRenderState.FoilType.SPECIAL);
        }

        overlayRenderState.setRenderType(RenderType.itemEntityTranslucentCull(overlayAtlas));
        properties.applyToLayer(overlayRenderState, itemDisplayContext);
        overlayRenderState.prepareQuadList().addAll(overlayQuads);

        quads = originalQuads;
    }
}
