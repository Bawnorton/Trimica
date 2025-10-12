package com.bawnorton.trimica.mixin.accessor;

import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Holder.Reference.class)
public interface Holder$ReferenceAccessor {
	@Invoker("bindValue")
	<T> void trimica$bindValue(T value);
}
