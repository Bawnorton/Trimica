package com.bawnorton.trimica.data.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ConventionalTags {
	public static final TagKey<Item> SHIELD_TOOLS = itemTag("tools/shield");
	public static final TagKey<Item> DYES = itemTag("dyes");
	public static final TagKey<Item> BLACK_DYES = itemTag("dyes/black");
	public static final TagKey<Item> BLUE_DYES = itemTag("dyes/blue");
	public static final TagKey<Item> BROWN_DYES = itemTag("dyes/brown");
	public static final TagKey<Item> CYAN_DYES = itemTag("dyes/cyan");
	public static final TagKey<Item> GRAY_DYES = itemTag("dyes/gray");
	public static final TagKey<Item> GREEN_DYES = itemTag("dyes/green");
	public static final TagKey<Item> LIGHT_BLUE_DYES = itemTag("dyes/light_blue");
	public static final TagKey<Item> LIGHT_GRAY_DYES = itemTag("dyes/light_gray");
	public static final TagKey<Item> LIME_DYES = itemTag("dyes/lime");
	public static final TagKey<Item> MAGENTA_DYES = itemTag("dyes/magenta");
	public static final TagKey<Item> ORANGE_DYES = itemTag("dyes/orange");
	public static final TagKey<Item> PINK_DYES = itemTag("dyes/pink");
	public static final TagKey<Item> PURPLE_DYES = itemTag("dyes/purple");
	public static final TagKey<Item> RED_DYES = itemTag("dyes/red");
	public static final TagKey<Item> WHITE_DYES = itemTag("dyes/white");
	public static final TagKey<Item> YELLOW_DYES = itemTag("dyes/yellow");
	public static final TagKey<Item> EMPTY_BUCKETS = itemTag("buckets/empty");

	private static TagKey<Item> itemTag(String name) {
		return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", name));
	}
}
