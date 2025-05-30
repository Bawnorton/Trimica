package com.bawnorton.trimica.tags;

import com.bawnorton.trimica.Trimica;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TrimicaTags {
    public static final TagKey<Item> MATERIAL_ADDITIONS = TagKey.create(Registries.ITEM, Trimica.rl("material_additions"));
    public static final TagKey<Item> SMITHING_BASE_BLACKLIST = TagKey.create(Registries.ITEM, Trimica.rl("smithing_base_blacklist"));
    public static final TagKey<Item> SMITHING_ADDITION_BLACKLIST = TagKey.create(Registries.ITEM, Trimica.rl("smithing_addition_blacklist"));
}
