package com.bawnorton.trimica.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(targets = "net/minecraft/core/MappedRegistry$3")
public interface MappedRegistry$PendingTagsAnonymousAccessor {
	//? if fabric {
	@Accessor("val$pendingContents")
	//?} else {
	/*@Accessor("val$map")
	*///?}
	<T> Map<TagKey<T>, List<Holder<T>>> trimica$val$pendingContents();
}
