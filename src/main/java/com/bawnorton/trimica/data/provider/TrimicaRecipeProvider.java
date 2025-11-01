package com.bawnorton.trimica.data.provider;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.data.recipe.MaterialAdditionRecipeBuilder;
import com.bawnorton.trimica.data.tags.TrimicaTags;
import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.item.trim.TrimicaTrimPatterns;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTrimRecipeBuilder;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static net.minecraft.data.recipes.RecipeProvider.getItemName;

//? if fabric {

public class TrimicaRecipeProvider extends FabricRecipeProvider {
	public TrimicaRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, TrimicaRegistriesDataProvider.addRegistries(registries));
	}

	public static Stream<VanillaRecipeProvider.TrimTemplate> toolTrims() {
		return Stream.of(
						Pair.of(TrimicaItems.SOVEREIGN_TRIM_TEMPLATE, TrimicaTrimPatterns.SOVEREIGN),
						Pair.of(TrimicaItems.SOAR_TRIM_TEMPLATE, TrimicaTrimPatterns.SOAR),
						Pair.of(TrimicaItems.HOLLOW_TRIM_TEMPLATE, TrimicaTrimPatterns.HOLLOW),
						Pair.of(TrimicaItems.CRYSTALLINE_TRIM_TEMPLATE, TrimicaTrimPatterns.CRYSTALLINE)
				)
				.map(pair -> new VanillaRecipeProvider.TrimTemplate(
						pair.getFirst(),
						pair.getSecond(),
						ResourceKey.create(
								Registries.RECIPE,
								Trimica.rl(getItemName(pair.getFirst()) + "_smithing_trim")
						)
				));
	}

	@Override
	protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
		return new RecipeProvider(provider, recipeOutput) {
			@Override
			public void buildRecipes() {
				materialAdditionRecipe(output, TrimicaTags.ALL_TRIMMABLES, TrimicaTags.MATERIAL_ADDITIONS);

				shaped(RecipeCategory.MISC, TrimicaItems.RAINBOWIFIER, 1)
						.define('R', Items.RED_DYE)
						.define('O', Items.ORANGE_DYE)
						.define('Y', Items.YELLOW_DYE)
						.define('G', Items.LIME_DYE)
						.define('C', Items.CYAN_DYE)
						.define('B', Items.BLUE_DYE)
						.define('I', Items.PURPLE_DYE)
						.define('V', Items.PINK_DYE)
						.define('X', TrimicaItems.ANIMATOR)
						.pattern("ROY")
						.pattern("VXG")
						.pattern("IBC")
						.unlockedBy("has_ingredients", has(ConventionalItemTags.DYES))
						.save(output);

				toolTrims().forEach(template -> {
							Holder.Reference<TrimPattern> reference = registries.lookupOrThrow(Registries.TRIM_PATTERN)
									.getOrThrow(template.patternId());
							SmithingTrimRecipeBuilder.smithingTrim(
											Ingredient.of(template.template()),
											tag(TrimicaTags.TRIMMABLE_TOOLS),
											tag(ItemTags.TRIM_MATERIALS),
											reference,
											RecipeCategory.MISC
									)
									.unlocks("has_smithing_trim_template", has(template.template()))
									.save(output, template.recipeId());
						});
			}

			public void materialAdditionRecipe(RecipeOutput output, TagKey<Item> baseTag, TagKey<Item> additionTag) {
				MaterialAdditionRecipeBuilder.materialAddition(RecipeCategory.MISC, tag(baseTag), tag(additionTag))
						.unlocks("has_ingredients", has(additionTag))
						.save(output, ResourceKey.create(
								Registries.RECIPE,
								additionTag.location()
						));
			}
		};
	}

	@Override
	public @NotNull String getName() {
		return "Trimica Recipe Provider";
	}
}
//?} else {
/*import net.neoforged.neoforge.common.Tags;

public class TrimicaRecipeProvider extends RecipeProvider {
	protected TrimicaRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		super(registries, output);
	}

	@Override
	protected void buildRecipes() {
		materialAdditionRecipe(output, TrimicaTags.MATERIAL_ADDITIONS);
		shaped(RecipeCategory.MISC, TrimicaItems.RAINBOWIFIER, 1)
				.define('R', Items.RED_DYE)
				.define('O', Items.ORANGE_DYE)
				.define('Y', Items.YELLOW_DYE)
				.define('G', Items.LIME_DYE)
				.define('C', Items.CYAN_DYE)
				.define('B', Items.BLUE_DYE)
				.define('I', Items.PURPLE_DYE)
				.define('V', Items.PINK_DYE)
				.define('X', TrimicaItems.ANIMATOR)
				.pattern("ROY")
				.pattern("VXG")
				.pattern("IBC")
				.unlockedBy("has_ingredients", has(Tags.Items.DYES))
				.save(output);
	}

	public void materialAdditionRecipe(RecipeOutput output, TagKey<Item> additionTag) {
		MaterialAdditionRecipeBuilder.materialAddition(RecipeCategory.MISC, tag(additionTag))
				.unlocks("has_ingredients", has(additionTag))
				.save(output, ResourceKey.create(
						Registries.RECIPE,
						additionTag.location()
				));
	}

	public static class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
			super(packOutput, registries);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
			return new TrimicaRecipeProvider(registries, output);
		}

		@Override
		public String getName() {
			return "Trimica Recipe Provider";
		}
	}
}
*///?}
