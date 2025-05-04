package com.bawnorton.trimica.mixin.crafting;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingTrimRecipe.class)
public abstract class SmithingTrimRecipeMixin {
    @Shadow @Final @Mutable
    Ingredient addition;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void allowAllItems(CallbackInfo ci) {
        addition = Ingredient.of(BuiltInRegistries.ITEM.stream().filter(item -> item != Items.AIR));
    }
}
