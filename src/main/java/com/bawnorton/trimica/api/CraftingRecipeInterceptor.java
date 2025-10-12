package com.bawnorton.trimica.api;

import com.bawnorton.trimica.crafting.DefaultCraftingRecipeInterceptor;
import com.bawnorton.trimica.tags.PostponedTagHolder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

/**
 * @see DefaultCraftingRecipeInterceptor for default behaviour
 */
@SuppressWarnings("unused")
public interface CraftingRecipeInterceptor {
	default HolderSet<Item> getTag(TagKey<Item> tagKey) {
		return PostponedTagHolder.getUnloadedTag(tagKey);
	}

	/**
	 * The {@link Ingredient} to use for the addition ingredient in a smithing trim recipe.
	 *
	 * @return current ingredient if no changes are made, or a new ingredient to use instead.
	 * @apiNote to get the content of a tag, use {@link #getTag(TagKey)}. Tags cannot be queried directly as they are not loaded yet.
	 */
	Ingredient getAdditionIngredient(@Nullable Ingredient current);

	/**
	 * The {@link Ingredient} to use for the base ingredient in a smithing trim recipe.
	 *
	 * @return current ingredient if no changes are made, or a new ingredient to use instead.
	 * @apiNote to get the content of a tag, use {@link #getTag(TagKey)}. Tags cannot be queried directly as they are not loaded yet.
	 */
	Ingredient getBaseIngredient(@Nullable Ingredient current);
}
