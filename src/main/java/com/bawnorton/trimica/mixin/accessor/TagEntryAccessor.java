package com.bawnorton.trimica.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@MixinEnvironment
@Mixin(TagEntry.class)
public interface TagEntryAccessor {
    @Accessor("tag")
    TagKey<Item> trimica$tag();
}
