//? if fabric {
package com.bawnorton.trimica.data.provider.platform.fabric;

import com.bawnorton.trimica.item.trim.TrimicaTrimMaterials;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;

import java.util.concurrent.CompletableFuture;

public class FabricTrimicaRegistriesDataProvider extends RegistriesDatapackGenerator {
	public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.TRIM_MATERIAL, TrimicaTrimMaterials::bootstrap);

	public FabricTrimicaRegistriesDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, addRegistries(registries));
	}

	public static CompletableFuture<HolderLookup.Provider> addRegistries(CompletableFuture<HolderLookup.Provider> lookupProvider) {
		return RegistryPatchGenerator.createLookup(lookupProvider, BUILDER)
				.thenApply(RegistrySetBuilder.PatchedRegistries::patches);
	}
}
//?}