//? if fabric {
package com.bawnorton.trimica.platform.fabric.data.provider;

import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.tags.TrimicaTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;

public class TrimicaTagProvider extends FabricTagProvider.ItemTagProvider {
    public TrimicaTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture, null);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        addToTag(ItemTags.TRIM_MATERIALS, TrimicaItems.RAINBOWIFIER);
        addToTag(TrimicaTags.MATERIAL_ADDITIONS, TrimicaItems.ANIMATOR, Items.GLOW_INK_SAC);
    }

    private void addToTag(TagKey<Item> tagKey, Item... items) {
        getOrCreateTagBuilder(tagKey).add(items);
    }
}
//?}