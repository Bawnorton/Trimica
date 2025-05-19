package com.bawnorton.trimica.tags;

import com.bawnorton.trimica.Trimica;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TrimicaTags {
    public static final TagKey<Item> MATERIAL_ADDITIONS = TagKey.create(Registries.ITEM, Trimica.rl("material_additions"));
}
