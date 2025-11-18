package com.bawnorton.trimica.mixin.registry;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.item.crafting.MaterialAdditionRecipe;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplays;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeDisplays.class)
public abstract class RecipeDisplaysMixin {
	@Inject(
			method = "bootstrap",
			at = @At("TAIL")
	)
	private static void registerAdditional(Registry<RecipeDisplay.Type<?>> registry, CallbackInfoReturnable<RecipeDisplay.Type<?>> cir) {
		Registry.register(registry, Trimica.rl("material_addition"), MaterialAdditionRecipe.Display.TYPE);
	}
}
