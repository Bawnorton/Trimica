package com.bawnorton.trimica.loot;

import com.bawnorton.trimica.mixin.accessor.CompositeEntryBaseAccessor;
import com.bawnorton.trimica.mixin.accessor.DynamicLootAccessor;
import com.bawnorton.trimica.mixin.accessor.LootItemAccessor;
import com.bawnorton.trimica.mixin.accessor.LootPoolAccessor;
import com.bawnorton.trimica.mixin.accessor.LootTableAccessor;
import com.bawnorton.trimica.mixin.accessor.NestedLootTableAccessor;
import com.bawnorton.trimica.mixin.accessor.TagEntryAccessor;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LootTableReader {
	public static List<Item> read(LootTableGetter getter, LootTable lootTable) {
		LootTableAccessor accessor = (LootTableAccessor) lootTable;
		List<LootPool> pools = accessor.trimica$pools();
		List<Item> items = new ArrayList<>();
		for (LootPool pool : pools) {
			List<LootPoolEntryContainer> entries = ((LootPoolAccessor) pool).trimica$entries();
			for (LootPoolEntryContainer entry : entries) {
				items.addAll(readEntry(getter, entry));
			}
		}
		return items;
	}

	private static List<Item> readEntry(LootTableGetter getter, LootPoolEntryContainer entry) {
		return switch (entry) {
			case CompositeEntryBaseAccessor compositeEntryBase -> {
				List<Item> items = new ArrayList<>();
				for (LootPoolEntryContainer lootPoolEntry : compositeEntryBase.trimica$children()) {
					items.addAll(readEntry(getter, lootPoolEntry));
				}
				yield items;
			}
			case LootPoolSingletonContainer singletonContainer -> switch (singletonContainer) {
				case DynamicLootAccessor dynamicLoot -> {
					ResourceLocation name = dynamicLoot.trimica$name();
					if (name.equals(DecoratedPotBlock.SHERDS_DYNAMIC_DROP_ID)) {
						yield List.of(Items.DECORATED_POT);
					} else if (name.equals(ShulkerBoxBlock.CONTENTS)) {
						yield List.of(Items.SHULKER_BOX);
					}
					yield List.of();
				}
				case LootItemAccessor lootItem -> List.of(lootItem.trimica$item().value());
				case NestedLootTableAccessor nestedLootTable -> {
					Either<ResourceKey<LootTable>, LootTable> value = nestedLootTable.trimica$contents();
					LootTable table = value.map(key -> getter.get(key.location()), Function.identity());
					yield read(getter, table);
				}
				case TagEntryAccessor tagEntry -> {
					TagKey<Item> tagKey = tagEntry.trimica$tag();
					List<Item> items = new ArrayList<>();
					BuiltInRegistries.ITEM.get(tagKey).ifPresent(named -> named.forEach(holder -> items.add(holder.value())));
					yield items;
				}
				default -> List.of();
			};
			default -> List.of();
		};
	}

	public interface LootTableGetter {
		LootTable get(ResourceLocation id);
	}
}
