package com.bawnorton.trimica.mixin.registry;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.crafting.MaterialAdditionRecipe;
import com.bawnorton.trimica.item.TrimicaItems;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltInRegistries.class)
abstract class BuiltInRegistriesMixin {
	@Inject(
			method = "createContents",
			at = @At("TAIL")
	)
	private static void createTrimicaContents(CallbackInfo ci) {
		MaterialAdditionRecipe.SERIALIZER = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Trimica.rl("material_addition"), new MaterialAdditionRecipe.Serializer());
		TrimicaItems.forEach((key, item) -> Registry.register(BuiltInRegistries.ITEM, key, item));
	}
}
