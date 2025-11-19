package com.bawnorton.trimica.data.provider;

import com.bawnorton.trimica.data.tags.ConventionalTags;
import com.bawnorton.trimica.extend.TagAppenderExtension;
import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.data.tags.TrimicaTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public interface TrimicaItemTagProvider {
	default void addTags(HolderLookup.Provider provider) {
		addToTag(ItemTags.TRIM_MATERIALS, TrimicaItems.RAINBOWIFIER);
		addToTag(TrimicaTags.MATERIAL_ADDITIONS, TrimicaItems.ANIMATOR, Items.GLOW_INK_SAC);
		addToTag(TrimicaTags.SMITHING_BASE_BLACKLIST, List.of(ItemTags.SKULLS, ItemTags.WOOL_CARPETS, ConventionalTags.EMPTY_BUCKETS));
		addToTag(TrimicaTags.SMITHING_BASE_BLACKLIST, Items.CARVED_PUMPKIN);
		addToTag(TrimicaTags.SMITHING_ADDITION_BLACKLIST, Items.AIR);
		addToTag(TrimicaTags.ALL_TRIMMABLES, List.of(ItemTags.TRIMMABLE_ARMOR, ConventionalTags.SHIELD_TOOLS));
	}

	TagAppender<Item, Item> tag(TagKey<Item> tagKey);

	default void addToTag(TagKey<Item> tagKey, Item... items) {
		tag(tagKey).add(items);
	}

	@SuppressWarnings("unchecked")
	default void addToTag(TagKey<Item> tagKey, TagKey<Item> tag) {
		((TagAppenderExtension<ResourceKey<Item>, Item>) tag(tagKey)).trimica$forceAddTag(tag);
	}

	@SuppressWarnings("unchecked")
	default void addToTag(TagKey<Item> tagKey, List<TagKey<Item>> tags) {
		TagAppenderExtension<ResourceKey<Item>, Item> appender = (TagAppenderExtension<ResourceKey<Item>, Item>) tag(tagKey);
		for (TagKey<Item> tag : tags) {
			appender.trimica$forceAddTag(tag);
		}
	}
}
