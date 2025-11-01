package com.bawnorton.trimica.data.tags;

import com.bawnorton.trimica.Trimica;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TrimicaTags {
	public static final TagKey<Item> MATERIAL_ADDITIONS = TagKey.create(Registries.ITEM, Trimica.rl("material_additions"));
	public static final TagKey<Item> SMITHING_BASE_BLACKLIST = TagKey.create(Registries.ITEM, Trimica.rl("smithing_base_blacklist"));
	public static final TagKey<Item> SMITHING_ADDITION_BLACKLIST = TagKey.create(Registries.ITEM, Trimica.rl("smithing_addition_blacklist"));
	public static final TagKey<Item> TRIMMABLE_TOOLS = TagKey.create(Registries.ITEM, Trimica.rl("trimmable_tools"));
	public static final TagKey<Item> ALL_TRIMMABLES = TagKey.create(Registries.ITEM, Trimica.rl("all_trimmables"));
	public static final TagKey<Item> TOOL_TRIM_TEMPLATES = TagKey.create(Registries.ITEM, Trimica.rl("tool_trim_templates"));
}
