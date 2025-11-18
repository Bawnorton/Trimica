//? if neoforge {
/*package com.bawnorton.trimica.data.provider.platform.neoforge;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.data.provider.TrimicaModelProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;

public class NeoTrimicaModelProvider extends ModelProvider implements TrimicaModelProvider {
	public NeoTrimicaModelProvider(PackOutput output) {
		super(output, Trimica.MOD_ID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		TrimicaModelProvider.super.generateItemModels(itemModels);
	}
}
*///?}