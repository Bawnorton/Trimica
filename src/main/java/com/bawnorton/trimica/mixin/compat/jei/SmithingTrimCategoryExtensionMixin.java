//? if jei {
/*package com.bawnorton.trimica.mixin.compat.jei;

import com.bawnorton.trimica.item.TrimicaItems;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.library.plugins.vanilla.anvil.SmithingTrimCategoryExtension;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingTrimCategoryExtension.class)
public abstract class SmithingTrimCategoryExtensionMixin {
    @Inject(
            method = "onDisplayedIngredientsUpdate(Lnet/minecraft/world/item/crafting/SmithingTrimRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotDrawable;Lmezz/jei/api/gui/ingredient/IRecipeSlotDrawable;Lmezz/jei/api/gui/ingredient/IRecipeSlotDrawable;Lmezz/jei/api/gui/ingredient/IRecipeSlotDrawable;Lmezz/jei/api/recipe/IFocusGroup;)V",
            at = @At("HEAD")
    )
    private void overrideAdditionDisplay(SmithingTrimRecipe recipe, IRecipeSlotDrawable templateSlot, IRecipeSlotDrawable baseSlot, IRecipeSlotDrawable additionSlot, IRecipeSlotDrawable outputSlot, IFocusGroup focuses, CallbackInfo ci) {
        IIngredientAcceptor<?> ingredientAcceptor = additionSlot.createDisplayOverrides();
        ingredientAcceptor.add(TrimicaItems.FAKE_ADDITION);
    }
}
*///?}