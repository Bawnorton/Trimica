//? if fabric {
package com.bawnorton.trimica.platform.fabric.data.provider;

import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.tags.TrimicaTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;

//? if >1.21.5 {
import net.minecraft.data.tags.TagAppender;
//?}

public class TrimicaTagProvider extends FabricTagProvider.ItemTagProvider {
    public TrimicaTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture, null);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        addToTag(ItemTags.TRIM_MATERIALS, TrimicaItems.RAINBOWIFIER);
        addToTag(TrimicaTags.MATERIAL_ADDITIONS, TrimicaItems.ANIMATOR, Items.GLOW_INK_SAC);
        addToTag(TrimicaTags.SMITHING_BASE_BLACKLIST, ItemTags.SKULLS, ItemTags.WOOL_CARPETS, ConventionalItemTags.EMPTY_BUCKETS);
        addToTag(TrimicaTags.SMITHING_BASE_BLACKLIST, Items.CARVED_PUMPKIN);
        addToTag(TrimicaTags.SMITHING_ADDITION_BLACKLIST, Items.AIR);
    }

    private void addToTag(TagKey<Item> tagKey, Item... items) {
        //? if >1.21.5 {
        valueLookupBuilder(tagKey).add(items);
        //?} else {
        /*getOrCreateTagBuilder(tagKey).add(items);
         *///?}
    }

    @SafeVarargs
    private void addToTag(TagKey<Item> tagKey, TagKey<Item>... tags) {
        //? if >1.21.5 {
        TagAppender<Item, Item> appender = valueLookupBuilder(tagKey);
        for (TagKey<Item> tag : tags) {
            appender.forceAddTag(tag);
        }
        //?} else {
        /*getOrCreateTagBuilder(tagKey).forceAddTags(tags);
         *///?}
    }
}
//?}