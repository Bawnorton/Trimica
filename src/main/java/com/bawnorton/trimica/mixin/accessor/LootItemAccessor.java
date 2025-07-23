package com.bawnorton.trimica.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@MixinEnvironment
@Mixin(LootItem.class)
public interface LootItemAccessor {
    @Accessor("item")
    Holder<Item> trimica$item();
}
