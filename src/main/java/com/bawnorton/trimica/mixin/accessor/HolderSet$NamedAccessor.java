package com.bawnorton.trimica.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(HolderSet.Named.class)
public interface HolderSet$NamedAccessor {
	@Invoker("<init>")
	static <T> HolderSet.Named<T> trimica$init(HolderOwner<T> owner, TagKey<T> key) {
		throw new AssertionError();
	}

	@Invoker("bind")
	<T> void trimica$bind(List<Holder<T>> content);
}
