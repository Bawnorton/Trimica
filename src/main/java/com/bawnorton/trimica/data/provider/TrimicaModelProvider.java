package com.bawnorton.trimica.data.provider;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.item.TrimicaItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

//? if fabric {
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public class TrimicaModelProvider extends FabricModelProvider {
	public TrimicaModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModels) {
//?} else {
/*public class TrimicaModelProvider extends ModelProvider {
	public TrimicaModelProvider(PackOutput output) {
		super(output, Trimica.MOD_ID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		*///?}
		itemModel(itemModels, TrimicaItems.RAINBOWIFIER);
		itemModel(itemModels, TrimicaItems.ANIMATOR);
		itemModel(itemModels, TrimicaItems.FAKE_ADDITION);
		itemModel(itemModels, TrimicaItems.SOVEREIGN_TRIM_TEMPLATE);
		itemModel(itemModels, TrimicaItems.SOAR_TRIM_TEMPLATE);
		itemModel(itemModels, TrimicaItems.HOLLOW_TRIM_TEMPLATE);
		itemModel(itemModels, TrimicaItems.CRYSTALLINE_TRIM_TEMPLATE);
	}

	private void itemModel(ItemModelGenerators generators, Item item) {
		ResourceLocation id = item.builtInRegistryHolder().key().location().withPrefix("item/");
		ResourceLocation modelLoc = ModelTemplates.FLAT_ITEM.create(item, TextureMapping.layer0(id), generators.modelOutput);
		generators.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelLoc));
	}
}