package com.bawnorton.trimica.client.mixin.accessor;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;
import java.util.function.Supplier;

@Mixin(BlockModelWrapper.class)
public interface BlockModelWrapperAccessor {
    @Accessor("quads")
    List<BakedQuad> trimica$quads();

    @Accessor("tints")
    List<ItemTintSource> trimica$tints();

    @Accessor("extents")
    Supplier<Vector3f[]> trimica$extents();

    @Accessor("properties")
    ModelRenderProperties trimica$properties();
}
