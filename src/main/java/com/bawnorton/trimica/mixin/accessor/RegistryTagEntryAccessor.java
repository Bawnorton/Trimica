package com.bawnorton.trimica.mixin.accessor;

import net.minecraft.tags.TagEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TagEntry.class)
public interface RegistryTagEntryAccessor {
	@Accessor("tag")
	boolean trimica$tag();

	@Accessor("required")
	boolean trimica$required();
}
