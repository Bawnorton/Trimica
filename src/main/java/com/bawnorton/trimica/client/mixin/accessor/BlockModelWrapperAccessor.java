package com.bawnorton.trimica.client.mixin.accessor;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(BlockModelWrapper.class)
public interface BlockModelWrapperAccessor {
    @Accessor("quads")
    List<BakedQuad> trimica$quads();
}
