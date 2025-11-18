//? if neoforge {
/*package com.bawnorton.trimica.data.provider.platform.neoforge;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.data.provider.TrimicaItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class NeoTrimicaItemTagProvider extends ItemTagsProvider implements TrimicaItemTagProvider {
	public NeoTrimicaItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, Trimica.MOD_ID);
	}

	@Override
	public void addTags(HolderLookup.Provider provider) {
		TrimicaItemTagProvider.super.addTags(provider);
	}

	@Override
	public TagAppender<Item, Item> tag(TagKey<Item> tagKey) {
		return super.tag(tagKey);
	}
}
*///?}