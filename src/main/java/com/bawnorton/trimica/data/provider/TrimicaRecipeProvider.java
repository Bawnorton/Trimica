package com.bawnorton.trimica.data.provider;

import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.data.recipe.MaterialAdditionRecipeBuilder;
import com.bawnorton.trimica.tags.TrimicaTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

//? if fabric {
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;

public class TrimicaRecipeProvider extends FabricRecipeProvider {
    public TrimicaRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        return new RecipeProvider(provider, recipeOutput) {
            @Override
            public void buildRecipes() {
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
                        .unlockedBy("has_ingredients", has(ConventionalItemTags.DYES))
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
