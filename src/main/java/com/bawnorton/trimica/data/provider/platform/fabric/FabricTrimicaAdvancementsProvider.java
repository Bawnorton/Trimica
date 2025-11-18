//? if fabric {
package com.bawnorton.trimica.data.provider.platform.fabric;

import com.bawnorton.trimica.data.provider.TrimicaAdvancementsProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class FabricTrimicaAdvancementsProvider extends FabricAdvancementProvider {
	public FabricTrimicaAdvancementsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup);
	}

	public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
		TrimicaAdvancementsProvider.generateAdvancement(registryLookup, consumer);
	}
}
//?}