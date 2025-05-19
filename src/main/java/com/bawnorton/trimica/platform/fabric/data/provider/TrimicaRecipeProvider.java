//? if fabric {
package com.bawnorton.trimica.platform.fabric.data.provider;

import com.bawnorton.trimica.platform.fabric.data.recipe.MaterialAdditionRecipeBuilder;
import com.bawnorton.trimica.tags.TrimicaTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

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
//?}