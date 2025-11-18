//? if fabric {
package com.bawnorton.trimica.data.provider.platform.fabric;

import com.bawnorton.trimica.data.provider.TrimicaModelProvider;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;

public class FabricTrimicaModelProvider extends FabricModelProvider implements TrimicaModelProvider {
	public FabricTrimicaModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {
		TrimicaModelProvider.super.generateItemModels(itemModelGenerator);
	}
}
//?}
