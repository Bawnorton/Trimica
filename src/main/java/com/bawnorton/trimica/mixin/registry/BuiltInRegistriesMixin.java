package com.bawnorton.trimica.mixin.registry;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.crafting.MaterialAdditionRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BuiltInRegistries.class)
public abstract class BuiltInRegistriesMixin {
	static {
		MaterialAdditionRecipe.SERIALIZER = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Trimica.rl("material_addition"), new MaterialAdditionRecipe.Serializer());
	}
}
