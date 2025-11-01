//? if rei {
/*package com.bawnorton.trimica.compat.rei;

import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.mixin.accessor.SmithingTrimRecipeAccessor;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.SmithingDisplay;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.type.trim.TrimPattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrimicaSmithingDisplayFactory {
    public static List<DefaultSmithingDisplay> create(RecipeHolder<SmithingTrimRecipe> recipe) {
        List<DefaultSmithingDisplay> displays = new ArrayList<>();
        Holder<TrimPattern> trimPattern = ((SmithingTrimRecipeAccessor) recipe.value()).trimica$pattern();
        EntryIngredient baseIngredient = EntryIngredients.ofIngredient(recipe.value().baseIngredient());
        EntryIngredient additionIngredient = EntryIngredients.of(TrimicaItems.FAKE_ADDITION);
        displays.add(new DefaultSmithingDisplay.Trimming(
                List.of(
                        recipe.value().templateIngredient().map(EntryIngredients::ofIngredient).orElse(EntryIngredient.empty()),
                        baseIngredient,
                        additionIngredient
                ),
                List.of(baseIngredient),
                Optional.of(SmithingDisplay.SmithingRecipeType.TRIM),
                Optional.of(recipe.id().location()),
                trimPattern
        ));
        return displays;
    }
}
*///?}