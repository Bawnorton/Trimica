package com.bawnorton.trimica.data.provider;

import com.bawnorton.bettertrims.data.TrimMaterialTags;
import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.data.tags.TrimicaRuntimeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TrimicaTrimMaterialTagProvider extends FabricTagProvider<TrimMaterial> {
	private final TrimicaRuntimeTags runtimeTags;

	public TrimicaTrimMaterialTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, Registries.TRIM_MATERIAL, registriesFuture);
		this.runtimeTags = Trimica.getRuntimeTags();
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		addItemsToTag(TrimMaterialTags.QUARTZ, Set.of(
				Items.QUARTZ,
				Items.QUARTZ_BLOCK,
				Items.QUARTZ_PILLAR,
				Items.QUARTZ_BRICKS,
				Items.CHISELED_QUARTZ_BLOCK,
				Items.SMOOTH_QUARTZ,
				Items.QUARTZ_SLAB,
				Items.QUARTZ_STAIRS,
				Items.SMOOTH_QUARTZ_SLAB,
				Items.SMOOTH_QUARTZ_STAIRS,
				Items.NETHER_QUARTZ_ORE
		));
		addItemsToTag(TrimMaterialTags.IRON, Set.of(
				Items.RAW_IRON,
				Items.IRON_INGOT,
				Items.IRON_NUGGET,
				Items.SHEARS,
				Items.BUCKET,
				Items.COMPASS,
				Items.FLINT_AND_STEEL,
				Items.MINECART,
				Items.HOPPER_MINECART,
				Items.TNT_MINECART,
				Items.FURNACE_MINECART,
				Items.COMMAND_BLOCK_MINECART,
				Items.IRON_ORE,
				Items.DEEPSLATE_IRON_ORE,
				Items.RAW_IRON_BLOCK,
				Items.IRON_BLOCK,
				Items.IRON_DOOR,
				Items.IRON_TRAPDOOR,
				Items.HEAVY_WEIGHTED_PRESSURE_PLATE,
				Items.ANVIL,
				Items.CHIPPED_ANVIL,
				Items.DAMAGED_ANVIL,
				Items.HOPPER,
				Items.LANTERN,
				Items.SOUL_LANTERN,
				Items.CAULDRON,
				Items.IRON_BARS,
				Items.IRON_CHAIN,
				Items.RAIL,
				Items.DETECTOR_RAIL,
				Items.ACTIVATOR_RAIL,
				Items.IRON_AXE,
				Items.IRON_HOE,
				Items.IRON_PICKAXE,
				Items.IRON_SHOVEL,
				Items.IRON_SWORD,
				Items.SHIELD,
				Items.IRON_HELMET,
				Items.IRON_CHESTPLATE,
				Items.IRON_LEGGINGS,
				Items.IRON_BOOTS,
				Items.IRON_HORSE_ARMOR
		));
		addItemsToTag(TrimMaterialTags.NETHERITE, Set.of(
				Items.NETHERITE_BLOCK,
				Items.NETHERITE_INGOT,
				Items.NETHERITE_AXE,
				Items.NETHERITE_HOE,
				Items.NETHERITE_PICKAXE,
				Items.NETHERITE_SHOVEL,
				Items.NETHERITE_SWORD,
				Items.NETHERITE_HELMET,
				Items.NETHERITE_CHESTPLATE,
				Items.NETHERITE_LEGGINGS,
				Items.NETHERITE_BOOTS
		));
		addItemsToTag(TrimMaterialTags.REDSTONE, Set.of(
				Items.REDSTONE,
				Items.REDSTONE_TORCH,
				Items.REPEATER,
				Items.COMPARATOR,
				Items.REDSTONE_ORE,
				Items.DEEPSLATE_REDSTONE_ORE,
				Items.REDSTONE_BLOCK,
				Items.REDSTONE_LAMP
		));
		addItemsToTag(TrimMaterialTags.COPPER, Set.of(
				Items.COPPER_BLOCK,
				Items.WAXED_COPPER_BLOCK,
				Items.EXPOSED_COPPER,
				Items.WAXED_EXPOSED_COPPER,
				Items.WEATHERED_COPPER,
				Items.WAXED_WEATHERED_COPPER,
				Items.OXIDIZED_COPPER,
				Items.WAXED_OXIDIZED_COPPER,
				Items.RAW_COPPER_BLOCK,
				Items.COPPER_ORE,
				Items.DEEPSLATE_COPPER_ORE,
				Items.CUT_COPPER,
				Items.WAXED_CUT_COPPER,
				Items.EXPOSED_CUT_COPPER,
				Items.WAXED_EXPOSED_CUT_COPPER,
				Items.WEATHERED_CUT_COPPER,
				Items.WAXED_WEATHERED_CUT_COPPER,
				Items.OXIDIZED_CUT_COPPER,
				Items.WAXED_OXIDIZED_CUT_COPPER,
				Items.CUT_COPPER_SLAB,
				Items.WAXED_CUT_COPPER_SLAB,
				Items.EXPOSED_CUT_COPPER_SLAB,
				Items.WAXED_EXPOSED_CUT_COPPER_SLAB,
				Items.WEATHERED_CUT_COPPER_SLAB,
				Items.WAXED_WEATHERED_CUT_COPPER_SLAB,
				Items.OXIDIZED_CUT_COPPER_SLAB,
				Items.WAXED_OXIDIZED_CUT_COPPER_SLAB,
				Items.CUT_COPPER_STAIRS,
				Items.WAXED_CUT_COPPER_STAIRS,
				Items.EXPOSED_CUT_COPPER_STAIRS,
				Items.WAXED_EXPOSED_CUT_COPPER_STAIRS,
				Items.WEATHERED_CUT_COPPER_STAIRS,
				Items.WAXED_WEATHERED_CUT_COPPER_STAIRS,
				Items.OXIDIZED_CUT_COPPER_STAIRS,
				Items.WAXED_OXIDIZED_CUT_COPPER_STAIRS,
				Items.COPPER_BULB,
				Items.WAXED_COPPER_BULB,
				Items.WEATHERED_COPPER_BULB,
				Items.WAXED_WEATHERED_COPPER_BULB,
				Items.EXPOSED_COPPER_BULB,
				Items.WAXED_EXPOSED_COPPER_BULB,
				Items.OXIDIZED_COPPER_BULB,
				Items.WAXED_OXIDIZED_COPPER_BULB,
				Items.COPPER_DOOR,
				Items.WAXED_COPPER_DOOR,
				Items.WEATHERED_COPPER_DOOR,
				Items.WAXED_WEATHERED_COPPER_DOOR,
				Items.EXPOSED_COPPER_DOOR,
				Items.WAXED_EXPOSED_COPPER_DOOR,
				Items.OXIDIZED_COPPER_DOOR,
				Items.WAXED_OXIDIZED_COPPER_DOOR,
				Items.COPPER_TRAPDOOR,
				Items.WAXED_COPPER_TRAPDOOR,
				Items.WEATHERED_COPPER_TRAPDOOR,
				Items.WAXED_WEATHERED_COPPER_TRAPDOOR,
				Items.EXPOSED_COPPER_TRAPDOOR,
				Items.WAXED_EXPOSED_COPPER_TRAPDOOR,
				Items.OXIDIZED_COPPER_TRAPDOOR,
				Items.WAXED_OXIDIZED_COPPER_TRAPDOOR,
				Items.CHISELED_COPPER,
				Items.WAXED_CHISELED_COPPER,
				Items.WEATHERED_CHISELED_COPPER,
				Items.WAXED_WEATHERED_CHISELED_COPPER,
				Items.EXPOSED_CHISELED_COPPER,
				Items.WAXED_EXPOSED_CHISELED_COPPER,
				Items.OXIDIZED_CHISELED_COPPER,
				Items.WAXED_OXIDIZED_CHISELED_COPPER,
				Items.COPPER_CHEST,
				Items.WAXED_COPPER_CHEST,
				Items.WEATHERED_COPPER_CHEST,
				Items.WAXED_WEATHERED_COPPER_CHEST,
				Items.EXPOSED_COPPER_CHEST,
				Items.WAXED_EXPOSED_COPPER_CHEST,
				Items.OXIDIZED_COPPER_CHEST,
				Items.WAXED_OXIDIZED_COPPER_CHEST,
				Items.LIGHTNING_ROD,
				Items.WAXED_LIGHTNING_ROD,
				Items.WEATHERED_LIGHTNING_ROD,
				Items.WAXED_WEATHERED_LIGHTNING_ROD,
				Items.EXPOSED_LIGHTNING_ROD,
				Items.WAXED_EXPOSED_LIGHTNING_ROD,
				Items.OXIDIZED_LIGHTNING_ROD,
				Items.WAXED_OXIDIZED_LIGHTNING_ROD,
				Items.COPPER_GOLEM_STATUE,
				Items.WAXED_COPPER_GOLEM_STATUE,
				Items.WEATHERED_COPPER_GOLEM_STATUE,
				Items.WAXED_WEATHERED_COPPER_GOLEM_STATUE,
				Items.EXPOSED_COPPER_GOLEM_STATUE,
				Items.WAXED_EXPOSED_COPPER_GOLEM_STATUE,
				Items.OXIDIZED_COPPER_GOLEM_STATUE,
				Items.WAXED_OXIDIZED_COPPER_GOLEM_STATUE,
				Items.COPPER_LANTERN.unaffected(),
				Items.COPPER_LANTERN.waxed(),
				Items.COPPER_LANTERN.weathered(),
				Items.COPPER_LANTERN.waxedWeathered(),
				Items.COPPER_LANTERN.exposed(),
				Items.COPPER_LANTERN.waxedExposed(),
				Items.COPPER_LANTERN.oxidized(),
				Items.COPPER_LANTERN.waxedOxidized(),
				Items.COPPER_TORCH,
				Items.COPPER_BARS.unaffected(),
				Items.COPPER_BARS.waxed(),
				Items.COPPER_BARS.weathered(),
				Items.COPPER_BARS.waxedWeathered(),
				Items.COPPER_BARS.exposed(),
				Items.COPPER_BARS.waxedExposed(),
				Items.COPPER_BARS.oxidized(),
				Items.COPPER_BARS.waxedOxidized(),
				Items.COPPER_CHAIN.unaffected(),
				Items.COPPER_CHAIN.waxed(),
				Items.COPPER_CHAIN.weathered(),
				Items.COPPER_CHAIN.waxedWeathered(),
				Items.COPPER_CHAIN.exposed(),
				Items.COPPER_CHAIN.waxedExposed(),
				Items.COPPER_CHAIN.oxidized(),
				Items.COPPER_CHAIN.waxedOxidized(),
				Items.COPPER_INGOT,
				Items.COPPER_NUGGET,
				Items.RAW_COPPER,
				Items.COPPER_HELMET,
				Items.COPPER_CHESTPLATE,
				Items.COPPER_LEGGINGS,
				Items.COPPER_BOOTS,
				Items.COPPER_AXE,
				Items.COPPER_HOE,
				Items.COPPER_PICKAXE,
				Items.COPPER_SHOVEL,
				Items.COPPER_SWORD,
				Items.COPPER_HORSE_ARMOR
		));
		addItemsToTag(TrimMaterialTags.GOLD, Set.of(
				Items.RAW_GOLD,
				Items.GOLD_INGOT,
				Items.GOLD_NUGGET,
				Items.GOLDEN_APPLE,
				Items.ENCHANTED_GOLDEN_APPLE,
				Items.GOLDEN_CARROT,
				Items.GLISTERING_MELON_SLICE,
				Items.CLOCK,
				Items.GOLD_ORE,
				Items.DEEPSLATE_GOLD_ORE,
				Items.NETHER_GOLD_ORE,
				Items.GILDED_BLACKSTONE,
				Items.RAW_GOLD_BLOCK,
				Items.GOLD_BLOCK,
				Items.BELL,
				Items.POWERED_RAIL,
				Items.LIGHT_WEIGHTED_PRESSURE_PLATE,
				Items.GOLDEN_AXE,
				Items.GOLDEN_HOE,
				Items.GOLDEN_PICKAXE,
				Items.GOLDEN_SHOVEL,
				Items.GOLDEN_SWORD,
				Items.GOLDEN_HELMET,
				Items.GOLDEN_CHESTPLATE,
				Items.GOLDEN_LEGGINGS,
				Items.GOLDEN_BOOTS,
				Items.GOLDEN_HORSE_ARMOR
		));
		addItemsToTag(TrimMaterialTags.EMERALD, Set.of(
				Items.EMERALD,
				Items.EMERALD_BLOCK,
				Items.EMERALD_ORE,
				Items.DEEPSLATE_EMERALD_ORE
		));
		addItemsToTag(TrimMaterialTags.DIAMOND, Set.of(
				Items.DIAMOND,
				Items.DIAMOND_BLOCK,
				Items.DIAMOND_ORE,
				Items.DEEPSLATE_DIAMOND_ORE,
				Items.DIAMOND_AXE,
				Items.DIAMOND_HOE,
				Items.DIAMOND_PICKAXE,
				Items.DIAMOND_SHOVEL,
				Items.DIAMOND_SWORD,
				Items.DIAMOND_HELMET,
				Items.DIAMOND_CHESTPLATE,
				Items.DIAMOND_LEGGINGS,
				Items.DIAMOND_BOOTS,
				Items.DIAMOND_HORSE_ARMOR
		));
		addItemsToTag(TrimMaterialTags.LAPIS, Set.of(
				Items.LAPIS_LAZULI,
				Items.LAPIS_BLOCK,
				Items.LAPIS_ORE,
				Items.DEEPSLATE_LAPIS_ORE
		));
		addItemsToTag(TrimMaterialTags.AMETHYST, Set.of(
				Items.AMETHYST_CLUSTER,
				Items.SMALL_AMETHYST_BUD,
				Items.MEDIUM_AMETHYST_BUD,
				Items.LARGE_AMETHYST_BUD,
				Items.BUDDING_AMETHYST,
				Items.AMETHYST_BLOCK,
				Items.AMETHYST_SHARD
		));
		addItemsToTag(TrimMaterialTags.RESIN, Set.of(
				Items.RESIN_CLUMP,
				Items.RESIN_BLOCK,
				Items.RESIN_BRICK,
				Items.RESIN_BRICKS,
				Items.RESIN_BRICK_SLAB,
				Items.RESIN_BRICK_STAIRS,
				Items.RESIN_BRICK_WALL,
				Items.CHISELED_RESIN_BRICKS
		));
	}

	private void addItemsToTag(TagKey<TrimMaterial> tagKey, Set<Item> items) {
		TagAppender<ResourceKey<TrimMaterial>, TrimMaterial> appender = builder(tagKey);
		for (Item item : items) {
			Holder.Reference<Item> itemReference = item.builtInRegistryHolder();
			TagKey<TrimMaterial> materialTagKey = runtimeTags.createMaterialKeyHolderForItem(itemReference).tagKey();
			appender.forceAddTag(materialTagKey);
		}
	}
}
