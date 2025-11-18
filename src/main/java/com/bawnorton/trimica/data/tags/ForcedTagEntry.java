package com.bawnorton.trimica.data.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;

import java.util.function.Predicate;

public final class ForcedTagEntry extends TagEntry {
	public ForcedTagEntry(ResourceLocation id) {
		super(id, true, true);
	}

	@Override
	public boolean verifyIfPresent(Predicate<ResourceLocation> elementPredicate, Predicate<ResourceLocation> tagPredicate) {
		return true;
	}
}
