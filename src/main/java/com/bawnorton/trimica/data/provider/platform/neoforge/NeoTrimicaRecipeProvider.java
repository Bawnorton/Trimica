//? if neoforge {
/*package com.bawnorton.trimica.data.provider.platform.neoforge;

import com.bawnorton.trimica.data.provider.TrimicaRecipeProvider;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class NeoTrimicaRecipeProvider extends RecipeProvider implements TrimicaRecipeProvider {
	protected NeoTrimicaRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		super(registries, output);
	}

	@Override
	protected void buildRecipes() {
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

	public static class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
			super(packOutput, registries);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
			return new NeoTrimicaRecipeProvider(registries, output);
		}

		@Override
		public String getName() {
			return "Trimica Recipe Provider";
		}
	}
}
*///?}