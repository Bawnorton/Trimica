//? if fabric {
package com.bawnorton.trimica.data.provider.platform.fabric;

import com.bawnorton.trimica.data.provider.TrimicaItemTagProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class FabricTrimicaItemTagProvider extends FabricTagProvider.ItemTagProvider implements TrimicaItemTagProvider {
	public FabricTrimicaItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void addTags(HolderLookup.Provider provider) {
		TrimicaItemTagProvider.super.addTags(provider);
	}

	@Override
	public TagAppender<Item, Item> tag(TagKey<Item> tagKey) {
		return super.valueLookupBuilder(tagKey);
	}
}
//?}