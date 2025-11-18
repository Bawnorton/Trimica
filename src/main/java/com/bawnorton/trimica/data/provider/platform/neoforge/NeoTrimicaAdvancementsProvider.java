//? if neoforge {
/*package com.bawnorton.trimica.data.provider.platform.neoforge;

import com.bawnorton.trimica.data.provider.TrimicaAdvancementsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NeoTrimicaAdvancementsProvider extends AdvancementProvider {
	public NeoTrimicaAdvancementsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup, List.of(TrimicaAdvancementsProvider::generateAdvancement));
	}
}
*///?}