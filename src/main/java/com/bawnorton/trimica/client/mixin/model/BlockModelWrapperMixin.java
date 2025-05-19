package com.bawnorton.trimica.client.mixin.model;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.extend.ItemStackRenderState$LayerRenderStateExtender;
import com.bawnorton.trimica.client.mixin.accessor.BlockModelWrapperAccessor;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mixin(BlockModelWrapper.class)
public abstract class BlockModelWrapperMixin {
    @Shadow @Final @Mutable
    private List<ItemTintSource> tints;

    @Shadow @Final @Mutable
    private List<BakedQuad> quads;

    @Shadow @Final @Mutable
    private Supplier<Vector3f[]> extents;

    @Shadow @Final @Mutable
    private ModelRenderProperties properties;

    @WrapMethod(
            method = "update"
    )
    private void createNewLayerRenderStateForTrimOverlay(ItemStackRenderState itemStackRenderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext itemDisplayContext, ClientLevel clientLevel, LivingEntity livingEntity, int i, Operation<Void> original) {
        Runnable callOriginal = () -> original.call(itemStackRenderState, stack, itemModelResolver, itemDisplayContext, clientLevel, livingEntity, i);
        if (!stack.has(DataComponents.TRIM)) {
            callOriginal.run();
            return;
        }

        ProfilerFiller profiler = Profiler.get();
        profiler.push("trimica:item_runtime_atlas");
        ArmorTrim trim = stack.get(DataComponents.TRIM);
        ItemModel newModel = TrimicaClient.getItemModelFactory().getOrCreateModel((ItemModel) this, stack, trim);
        profiler.pop();
        profiler.push("trimica:item_overlay");
        List<ItemTintSource> originalTints = new ArrayList<>(tints);
        List<BakedQuad> originalQuads = new ArrayList<>(quads);
        Vector3f[] originalExtents = extents.get();
        Supplier<Vector3f[]> originalExtentsSupplier = () -> originalExtents;
        ModelRenderProperties originalProperties = new ModelRenderProperties(
                properties.usesBlockLight(),
                properties.particleIcon(),
                properties.transforms()
        );
        if(newModel instanceof BlockModelWrapperAccessor blockModelWrapper) {
            tints = blockModelWrapper.trimica$tints();
            quads = blockModelWrapper.trimica$quads();
            extents = blockModelWrapper.trimica$extents();
            properties = blockModelWrapper.trimica$properties();
        }

        int lastBaseTextureIndex = -1;
        ResourceLocation overlayAtlas = null;
        for (int j = 0; j < quads.size(); j++) {
            BakedQuad quad = quads.get(j);
            if (quad.sprite().atlasLocation().getNamespace().equals(Trimica.MOD_ID)) {
                lastBaseTextureIndex = j;
                overlayAtlas = quad.sprite().atlasLocation();
                break;
            }
        }
        if (overlayAtlas == null) {
            tints = originalTints;
            quads = originalQuads;
            extents = originalExtentsSupplier;
            properties = originalProperties;
            callOriginal.run();
            return;
        }

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
        profiler.pop();
    }
}
