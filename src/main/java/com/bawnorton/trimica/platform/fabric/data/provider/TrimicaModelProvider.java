//? if fabric {
package com.bawnorton.trimica.platform.fabric.data.provider;

import com.bawnorton.trimica.item.TrimicaItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class TrimicaModelProvider extends FabricModelProvider {
    public TrimicaModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {

    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        itemModel(generators, TrimicaItems.RAINBOWIFIER);
        itemModel(generators, TrimicaItems.ANIMATOR);
        itemModel(generators, TrimicaItems.FAKE_ADDITION);
    }

    private void itemModel(ItemModelGenerators generators, Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item).withPrefix("item/");
        ResourceLocation modelLoc = ModelTemplates.FLAT_ITEM.create(item, TextureMapping.layer0(id), generators.modelOutput);
        generators.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelLoc));
    }
}
//?}