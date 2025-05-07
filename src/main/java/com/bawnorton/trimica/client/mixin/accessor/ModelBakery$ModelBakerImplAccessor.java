package com.bawnorton.trimica.client.mixin.accessor;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.SpriteGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModelBakery.ModelBakerImpl.class)
public interface ModelBakery$ModelBakerImplAccessor {
    @Invoker("<init>")
    static ModelBakery.ModelBakerImpl trimic$init(ModelBakery modelBakery, SpriteGetter spriteGetter) {
        throw new AssertionError();
    }
}
