package com.bawnorton.trimica.item;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.TrimicaToggles;
import com.bawnorton.trimica.item.trim.TrimicaTrimMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class TrimicaItems {
	private static final List<ItemHolder> ITEMS = new ArrayList<>();

	public static final Item RAINBOWIFIER = createIf(
			TrimicaToggles.enableRainbowifier,
			"rainbowifier",
			Item::new,
			new Item.Properties().rarity(Rarity.UNCOMMON).trimMaterial(TrimicaTrimMaterials.RAINBOW)
	);

	public static final Item ANIMATOR = createIf(
			TrimicaToggles.enableAnimator,
			"animator",
			Item::new,
			new Item.Properties().rarity(Rarity.UNCOMMON)
	);

	public static final Item FAKE_ADDITION = createIf(
			true,
			"fake_addition",
			Item::new,
			new Item.Properties()
	);

	public static final Item SOVEREIGN_TRIM_TEMPLATE = createIf(
			TrimicaToggles.enableToolTrims,
			"sovereign_trim_template",
			SmithingToolTemplateItem::new,
			new Item.Properties().rarity(Rarity.RARE)
	);

	public static final Item SOAR_TRIM_TEMPLATE = createIf(
			TrimicaToggles.enableToolTrims,
			"soar_trim_template",
			SmithingToolTemplateItem::new,
			new Item.Properties().rarity(Rarity.RARE)
	);

	public static final Item HOLLOW_TRIM_TEMPLATE = createIf(
			TrimicaToggles.enableToolTrims,
			"hollow_trim_template",
			SmithingToolTemplateItem::new,
			new Item.Properties().rarity(Rarity.RARE)
	);

	public static final Item CRYSTALLINE_TRIM_TEMPLATE = createIf(
			TrimicaToggles.enableToolTrims,
			"crystalline_trim_template",
			SmithingToolTemplateItem::new,
			new Item.Properties().rarity(Rarity.EPIC)
	);

	public static void forEach(BiConsumer<ResourceKey<Item>, Item> consumer) {
		for (ItemHolder holder : ITEMS) {
			consumer.accept(holder.key(), holder.item());
		}
	}

	private static Item create(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Trimica.rl(name));
		properties.setId(key);
		Item item = factory.apply(properties);
		ITEMS.add(new ItemHolder(key, item));
		return item;
	}

	/**
	 * Overriding inferred nullability because all usages are guarded by condition checks.
	 * Avoids unnecessary "May be null" warnings.
	 */
	@NotNull
	private static Item createIf(boolean condition, String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
		return TrimicaToggles.enableItems && condition ? create(name, factory, properties) : null;
	}

	private record ItemHolder(ResourceKey<Item> key, Item item) {
	}
}