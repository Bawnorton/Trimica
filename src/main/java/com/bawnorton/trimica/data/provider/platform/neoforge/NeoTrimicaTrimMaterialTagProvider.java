//? if neoforge {
/*package com.bawnorton.trimica.data.provider.platform.neoforge;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.data.provider.TrimicaTrimMaterialTagProvider;
import com.bawnorton.trimica.data.tags.TrimicaRuntimeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

import java.util.concurrent.CompletableFuture;

public class NeoTrimicaTrimMaterialTagProvider extends TagsProvider<TrimMaterial> implements TrimicaTrimMaterialTagProvider {
	private final TrimicaRuntimeTags runtimeTags;

	public NeoTrimicaTrimMaterialTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, Registries.TRIM_MATERIAL, lookupProvider, Trimica.MOD_ID);
		this.runtimeTags = Trimica.getRuntimeTags();
	}

	@Override
	public void addTags(HolderLookup.Provider wrapperLookup) {
		TrimicaTrimMaterialTagProvider.super.addTags(wrapperLookup);
	}

	@Override
	public TagAppender<ResourceKey<TrimMaterial>, TrimMaterial> builder(TagKey<TrimMaterial> tagKey) {
		return TagAppender.forBuilder(super.getOrCreateRawBuilder(tagKey));
	}

	@Override
	public TrimicaRuntimeTags getRuntimeTags() {
		return runtimeTags;
	}
}
*///?}