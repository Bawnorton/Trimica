package com.bawnorton.trimica.data.recipe;

import com.bawnorton.trimica.crafting.MaterialAdditionRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.LinkedHashMap;
import java.util.Map;

public class MaterialAdditionRecipeBuilder {
	private final RecipeCategory category;
	private final Ingredient base;
	private final Ingredient addition;
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

	public MaterialAdditionRecipeBuilder(RecipeCategory recipeCategory, Ingredient base, Ingredient addition) {
		this.category = recipeCategory;
		this.base = base;
		this.addition = addition;
	}

	public static MaterialAdditionRecipeBuilder materialAddition(RecipeCategory recipeCategory, Ingredient base, Ingredient addition) {
		return new MaterialAdditionRecipeBuilder(recipeCategory, base, addition);
	}

	public MaterialAdditionRecipeBuilder unlocks(String string, Criterion<?> criterion) {
		this.criteria.put(string, criterion);
		return this;
	}

	public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> resourceKey) {
		this.ensureValid(resourceKey);
		Advancement.Builder builder = recipeOutput.advancement()
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
				.rewards(AdvancementRewards.Builder.recipe(resourceKey))
				.requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(builder::addCriterion);
		MaterialAdditionRecipe materialAdditionRecipe = new MaterialAdditionRecipe(this.base, this.addition);
		recipeOutput.accept(resourceKey, materialAdditionRecipe, builder.build(resourceKey.location().withPrefix("recipes/" + this.category.getFolderName() + "/")));
	}

	private void ensureValid(ResourceKey<Recipe<?>> resourceKey) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + resourceKey.location());
		}
	}
}
