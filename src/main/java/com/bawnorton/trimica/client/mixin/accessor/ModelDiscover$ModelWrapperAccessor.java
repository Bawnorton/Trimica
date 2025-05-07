package com.bawnorton.trimica.client.mixin.accessor;

import net.minecraft.client.resources.model.ModelDiscovery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModelDiscovery.ModelWrapper.class)
public interface ModelDiscover$ModelWrapperAccessor {
    @Invoker("<init>")
    static ModelDiscovery.ModelWrapper trimica$init(ResourceLocation resourceLocation, UnbakedModel unbakedModel, boolean bl) {
        throw new AssertionError();
    }

    @Accessor("parent")
    void trimica$parent(ModelDiscovery.ModelWrapper parent);
}
