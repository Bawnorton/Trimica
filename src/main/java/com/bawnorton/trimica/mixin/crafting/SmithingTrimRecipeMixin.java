package com.bawnorton.trimica.mixin.crafting;

import com.bawnorton.trimica.api.impl.TrimicaApiImpl;
import com.bawnorton.trimica.data.TrimicaDataGen;
import com.bawnorton.trimica.item.component.AdditionalTrims;
import com.bawnorton.trimica.trim.TrimMaterialRuntimeRegistry;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(SmithingTrimRecipe.class)
public abstract class SmithingTrimRecipeMixin {
	@Shadow
	@Final
	@Mutable
	Ingredient addition;

	@Shadow
	@Final
	@Mutable
	Ingredient base;

	@Inject(
			method = "<init>",
			at = @At("TAIL")
	)
	private void useTrimicaIngredients(CallbackInfo ci) {
		if (TrimMaterialRuntimeRegistry.enableTrimEverything && !TrimicaDataGen.duringDataGen) {
			addition = TrimicaApiImpl.INSTANCE.applyCraftingRecipeInterceptorsForAddition(addition);
			base = TrimicaApiImpl.INSTANCE.applyCraftingRecipeInterceptorsForBase(base);
		}
	}

	@WrapMethod(
			method = "applyTrim"
	)
	private static ItemStack applyAdditionalTrims(HolderLookup.Provider registries, ItemStack base, ItemStack addition, Holder<TrimPattern> pattern, Operation<ItemStack> original) {
		if (!AdditionalTrims.enableAdditionalTrims) {
			return original.call(registries, base, addition, pattern);
		}

		AdditionalTrims.correctTrimComponents(base);
		ArmorTrim existingTrim = base.get(DataComponents.TRIM);
		if (existingTrim == null) {
			return original.call(registries, base, addition, pattern);
		}

		Optional<Holder<TrimMaterial>> optionalMaterial = TrimMaterials.getFromIngredient(registries, addition);
		if (optionalMaterial.isEmpty()) {
			return ItemStack.EMPTY;
		}

		Holder<TrimMaterial> material = optionalMaterial.get();
		ArmorTrim newTrim = new ArmorTrim(material, pattern);
		if (AdditionalTrims.hasTrim(base, newTrim)) {
			return ItemStack.EMPTY;
		}

		ItemStack result = base.copyWithCount(1);
		if (AdditionalTrims.tryAddTrim(result, newTrim)) {
			return result;
		} else {
			return ItemStack.EMPTY;
		}
	}
}
