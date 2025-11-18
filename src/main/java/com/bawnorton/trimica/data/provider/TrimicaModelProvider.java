package com.bawnorton.trimica.data.provider;

import com.bawnorton.trimica.item.TrimicaItems;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public interface TrimicaModelProvider {
	default void generateItemModels(ItemModelGenerators itemModels) {
		itemModel(itemModels, TrimicaItems.RAINBOWIFIER);
		itemModel(itemModels, TrimicaItems.ANIMATOR);
		itemModel(itemModels, TrimicaItems.FAKE_ADDITION);
	}

	private void itemModel(ItemModelGenerators generators, Item item) {
		ResourceLocation id = item.builtInRegistryHolder().key().location().withPrefix("item/");
		ResourceLocation modelLoc = ModelTemplates.FLAT_ITEM.create(item, TextureMapping.layer0(id), generators.modelOutput);
		generators.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelLoc));
	}
}