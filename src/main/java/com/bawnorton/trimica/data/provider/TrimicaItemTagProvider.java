package com.bawnorton.trimica.data.provider;

//? if fabric {
import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.data.tags.TrimicaTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.tags.TagAppender;

public class TrimicaItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public TrimicaItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture, null);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        addToTag(ItemTags.TRIM_MATERIALS, TrimicaItems.RAINBOWIFIER);
				addToTag(ItemTags.TRIMMABLE_ARMOR, Items.SHIELD);
        addToTag(TrimicaTags.MATERIAL_ADDITIONS, TrimicaItems.ANIMATOR, Items.GLOW_INK_SAC);
        addToTag(TrimicaTags.SMITHING_BASE_BLACKLIST, ItemTags.SKULLS, ItemTags.WOOL_CARPETS, ConventionalItemTags.EMPTY_BUCKETS);
        addToTag(TrimicaTags.SMITHING_BASE_BLACKLIST, Items.CARVED_PUMPKIN);
        addToTag(TrimicaTags.SMITHING_ADDITION_BLACKLIST, Items.AIR);
				addToTag(TrimicaTags.TRIMMABLE_TOOLS, ConventionalItemTags.MINING_TOOL_TOOLS, ConventionalItemTags.MELEE_WEAPON_TOOLS);
				addToTag(TrimicaTags.ALL_TRIMMABLES, ItemTags.TRIMMABLE_ARMOR, TrimicaTags.TRIMMABLE_TOOLS);
				addToTag(TrimicaTags.TOOL_TRIM_TEMPLATES, TrimicaItems.SOVEREIGN_TRIM_TEMPLATE, TrimicaItems.SOAR_TRIM_TEMPLATE, TrimicaItems.HOLLOW_TRIM_TEMPLATE, TrimicaItems.CRYSTALLINE_TRIM_TEMPLATE);
    }

    private void addToTag(TagKey<Item> tagKey, Item... items) {
        valueLookupBuilder(tagKey).add(items);
    }

    @SafeVarargs
    private void addToTag(TagKey<Item> tagKey, TagKey<Item>... tags) {
        TagAppender<Item, Item> appender = valueLookupBuilder(tagKey);
        for (TagKey<Item> tag : tags) {
            appender.forceAddTag(tag);
        }
    }
}
//?} else {
/*
import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.tags.TrimicaTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class TrimicaItemTagProvider extends ItemTagsProvider {
	public TrimicaItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, Trimica.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		addToTag(net.minecraft.tags.ItemTags.TRIM_MATERIALS, TrimicaItems.RAINBOWIFIER);
		addToTag(TrimicaTags.MATERIAL_ADDITIONS, TrimicaItems.ANIMATOR, Items.GLOW_INK_SAC);
		addToTag(TrimicaTags.SMITHING_BASE_BLACKLIST, ItemTags.SKULLS, ItemTags.WOOL_CARPETS, Tags.Items.BUCKETS_EMPTY);
		addToTag(TrimicaTags.SMITHING_BASE_BLACKLIST, Items.CARVED_PUMPKIN);
		addToTag(TrimicaTags.SMITHING_ADDITION_BLACKLIST, Items.AIR);
	}

	private void addToTag(TagKey<Item> tagKey, Item... items) {
		tag(tagKey).add(items);
	}

	@SafeVarargs
	private void addToTag(TagKey<Item> tagKey, TagKey<Item>... tags) {
		TagAppender<Item, Item> appender = tag(tagKey);
		for (TagKey<Item> tag : tags) {
			appender.addTag(tag);
		}
	}
}
*///?}
