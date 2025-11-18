//? if fabric {
package com.bawnorton.trimica.data.provider.platform.fabric;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.data.provider.TrimicaTrimMaterialTagProvider;
import com.bawnorton.trimica.data.tags.TrimicaRuntimeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

import java.util.concurrent.CompletableFuture;

public class FabricTrimicaTrimMaterialTagProvider extends FabricTagProvider<TrimMaterial> implements TrimicaTrimMaterialTagProvider {
	private final TrimicaRuntimeTags runtimeTags;

	public FabricTrimicaTrimMaterialTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, Registries.TRIM_MATERIAL, registriesFuture);
		this.runtimeTags = Trimica.getRuntimeTags();
	}

	@Override
	public void addTags(HolderLookup.Provider wrapperLookup) {
		TrimicaTrimMaterialTagProvider.super.addTags(wrapperLookup);
	}

	@Override
	public TagAppender<ResourceKey<TrimMaterial>, TrimMaterial> builder(TagKey<TrimMaterial> tagKey) {
		return super.builder(tagKey);
	}

	@Override
	public TrimicaRuntimeTags getRuntimeTags() {
		return runtimeTags;
	}
}
//?}