package com.bawnorton.trimica.data;

//? if fabric {

import com.bawnorton.trimica.data.provider.*;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.DetectedVersion;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;

@Entrypoint("fabric-datagen")
public final class TrimicaDataGen implements DataGeneratorEntrypoint {
	public static boolean duringDataGen = false;

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		duringDataGen = true;
		FabricDataGenerator.Pack mainPack = fabricDataGenerator.createPack();
		mainPack.addProvider((FabricDataGenerator.Pack.Factory<PackMetadataGenerator>) output -> new PackMetadataGenerator(output)
				.add(
						//? if >=1.21.10 {
						PackMetadataSection.SERVER_TYPE,
						new PackMetadataSection(
								Component.literal("${mod_description}"),
								InclusiveRange.create(
										DetectedVersion.BUILT_IN.packVersion(PackType.SERVER_DATA),
										DetectedVersion.BUILT_IN.packVersion(PackType.SERVER_DATA)
								).getOrThrow()
						)
						//?} else {
						/*PackMetadataSection.TYPE,
						new PackMetadataSection(
								Component.literal("Trimica Datapack"),
								DetectedVersion.BUILT_IN.packVersion(PackType.SERVER_DATA),
								Optional.empty()
						)
						*///?}
				)
		);
		mainPack.addProvider(TrimicaRegistriesDataProvider::new);
		mainPack.addProvider(TrimicaItemTagProvider::new);
		mainPack.addProvider(TrimicaTrimMaterialTagProvider::new);
		mainPack.addProvider(TrimicaRecipeProvider::new);
		mainPack.addProvider(TrimicaAdvancementsProvider::new);
		mainPack.addProvider(TrimicaModelProvider::new);
	}
}
//?} else {
/*
import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.data.provider.TrimicaAdvancementsProvider;
import com.bawnorton.trimica.data.provider.TrimicaModelProvider;
import com.bawnorton.trimica.data.provider.TrimicaRecipeProvider;
import com.bawnorton.trimica.data.provider.TrimicaTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Trimica.MOD_ID)
public final class TrimicaDataGen {
	@SubscribeEvent
	public static void gatherServerData(GatherDataEvent.Server event) {
		DataGenerator gen = event.getGenerator();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		PackOutput mainPack = gen.getPackOutput();
		gen.addProvider(true, new TrimicaAdvancementsProvider(mainPack, lookupProvider));
		gen.addProvider(true, new TrimicaRecipeProvider.Runner(mainPack, lookupProvider));
		gen.addProvider(true, new TrimicaTagProvider(mainPack, lookupProvider));
	}

	@SubscribeEvent
	public static void gatherClientData(GatherDataEvent.Client event) {
		DataGenerator gen = event.getGenerator();
		PackOutput mainPack = gen.getPackOutput();
		gen.addProvider(true, new TrimicaModelProvider(mainPack));
	}
}

*///?}