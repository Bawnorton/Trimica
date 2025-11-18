package com.bawnorton.trimica.data.tags;

import com.bawnorton.trimica.Trimica;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class TrimicaTags {
	public static final TagKey<Item> MATERIAL_ADDITIONS = tag("material_additions");
	public static final TagKey<Item> SMITHING_BASE_BLACKLIST = tag("smithing_base_blacklist");
	public static final TagKey<Item> SMITHING_ADDITION_BLACKLIST = tag("smithing_addition_blacklist");
	public static final TagKey<Item> ALL_TRIMMABLES = tag("all_trimmables");

	private static TagKey<Item> tag(String name) {
		return TagKey.create(Registries.ITEM, Trimica.rl(name));
	}
}
