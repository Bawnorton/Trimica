package com.bawnorton.trimica.client.mixin.accessor;

import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpecialModelWrapper.class)
public interface SpecialModelWrapperAccessor {
    @Accessor("properties")
    ModelRenderProperties trimica$properties();
}
