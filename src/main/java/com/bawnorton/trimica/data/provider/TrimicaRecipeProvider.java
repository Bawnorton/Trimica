package com.bawnorton.trimica.data.provider;

import com.bawnorton.trimica.data.recipe.MaterialAdditionRecipeBuilder;
import com.bawnorton.trimica.data.tags.ConventionalTags;
import com.bawnorton.trimica.data.tags.TrimicaTags;
import com.bawnorton.trimica.item.TrimicaItems;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

public interface TrimicaRecipeProvider {
	default void buildRecipes(RecipeOutput output) {
		materialAdditionRecipe(output, TrimicaTags.ALL_TRIMMABLES, TrimicaTags.MATERIAL_ADDITIONS);
		shaped(RecipeCategory.MISC, TrimicaItems.RAINBOWIFIER, 1)
				.define('R', ConventionalTags.RED_DYES)
				.define('O', ConventionalTags.ORANGE_DYES)
				.define('Y', ConventionalTags.YELLOW_DYES)
				.define('G', ConventionalTags.LIME_DYES)
				.define('C', ConventionalTags.CYAN_DYES)
				.define('B', ConventionalTags.BLUE_DYES)
				.define('I', ConventionalTags.PURPLE_DYES)
				.define('V', ConventionalTags.PINK_DYES)
				.define('X', TrimicaItems.ANIMATOR)
				.pattern("ROY")
				.pattern("VXG")
				.pattern("IBC")
				.unlockedBy("has_ingredients", has(ConventionalTags.DYES))
				.save(output);
	}

	default void materialAdditionRecipe(RecipeOutput output, TagKey<Item> baseTag, TagKey<Item> additionTag) {
		MaterialAdditionRecipeBuilder.materialAddition(RecipeCategory.MISC, tag(baseTag), tag(additionTag))
				.unlocks("has_ingredients", has(additionTag))
				.save(output, ResourceKey.create(
						Registries.RECIPE,
						additionTag.location()
				));
	}

	ShapedRecipeBuilder shaped(RecipeCategory recipeCategory, Item rainbowifier, int count);

	Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag);

	Ingredient tag(TagKey<Item> tagKey);
}