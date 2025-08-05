package com.bawnorton.trimica.mixin.crafting;

import com.bawnorton.trimica.api.impl.TrimicaApiImpl;
import com.bawnorton.trimica.trim.TrimMaterialRuntimeRegistry;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment
@Mixin(SmithingTrimRecipe.class)
public abstract class SmithingTrimRecipeMixin {
    @Shadow @Final @Mutable
    Ingredient addition;

    @Shadow @Final @Mutable
    Ingredient base;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void useTrimicaIngredients(CallbackInfo ci) {
        if (TrimMaterialRuntimeRegistry.enableTrimEverything) {
            addition = TrimicaApiImpl.INSTANCE.applyCraftingRecipeInterceptorsForAddition(addition);
            base = TrimicaApiImpl.INSTANCE.applyCraftingRecipeInterceptorsForBase(base);
        }
    }
}
