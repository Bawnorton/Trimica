package com.bawnorton.trimica.data.provider;

import com.bawnorton.trimica.item.trim.TrimicaTrimMaterials;
import com.bawnorton.trimica.item.trim.TrimicaTrimPatterns;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;

import java.util.concurrent.CompletableFuture;

public class TrimicaRegistriesDataProvider extends RegistriesDatapackGenerator {
	public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.TRIM_PATTERN, TrimicaTrimPatterns::bootstrap)
			.add(Registries.TRIM_MATERIAL, TrimicaTrimMaterials::bootstrap);

	public TrimicaRegistriesDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, addRegistries(registries));
	}

	public static CompletableFuture<HolderLookup.Provider> addRegistries(CompletableFuture<HolderLookup.Provider> lookupProvider) {
		return RegistryPatchGenerator.createLookup(lookupProvider, BUILDER)
				.thenApply(RegistrySetBuilder.PatchedRegistries::patches);
	}
}