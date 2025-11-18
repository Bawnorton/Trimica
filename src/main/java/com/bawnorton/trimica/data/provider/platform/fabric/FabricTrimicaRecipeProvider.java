//? if fabric {
package com.bawnorton.trimica.data.provider.platform.fabric;

import com.bawnorton.trimica.data.provider.TrimicaRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class FabricTrimicaRecipeProvider extends FabricRecipeProvider {
	public FabricTrimicaRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
		return new RecipeProviderImpl(registryLookup, exporter);
	}

	@Override
	public @NotNull String getName() {
		return "Trimica Recipe Provider";
	}

	private static class RecipeProviderImpl extends RecipeProvider implements TrimicaRecipeProvider {
		protected RecipeProviderImpl(HolderLookup.Provider registries, RecipeOutput output) {
			super(registries, output);
		}

		@Override
		public void buildRecipes() {
			TrimicaRecipeProvider.super.buildRecipes(output);
		}

		@Override
		public ShapedRecipeBuilder shaped(RecipeCategory recipeCategory, Item rainbowifier, int count) {
			return super.shaped(recipeCategory, rainbowifier, count);
		}

		@Override
		public Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
			return super.has(tag);
		}

		@Override
		public Ingredient tag(TagKey<Item> tagKey) {
			return super.tag(tagKey);
		}
	}
}
//?}