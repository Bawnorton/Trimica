//? if neoforge {
/*package com.bawnorton.trimica.data.provider.platform.neoforge;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.item.trim.TrimicaTrimMaterials;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class NeoTrimicaRegistriesDataProvider extends DatapackBuiltinEntriesProvider {
	public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.TRIM_MATERIAL, TrimicaTrimMaterials::bootstrap);

	public NeoTrimicaRegistriesDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, BUILDER, Set.of(Trimica.MOD_ID));
	}
}
*///?}