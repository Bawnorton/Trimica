package com.bawnorton.trimica.item.crafting;

import com.bawnorton.trimica.api.CraftingRecipeInterceptor;
import com.bawnorton.trimica.platform.Platform;
import com.bawnorton.trimica.data.tags.TrimicaTags;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.equipment.Equippable;

public class DefaultCraftingRecipeInterceptor implements CraftingRecipeInterceptor {
	@Override
	public Ingredient getAdditionIngredient(Ingredient current) {
		return Ingredient.of(BuiltInRegistries.ITEM.stream().filter(this::allowAsAddition));
	}

	private boolean allowAsAddition(Item item) {
		HolderSet<Item> blacklisted = getTag(TrimicaTags.SMITHING_ADDITION_BLACKLIST);
		return !blacklisted.contains(item.builtInRegistryHolder());
	}

	@Override
	public Ingredient getBaseIngredient(Ingredient current) {
		return Ingredient.of(BuiltInRegistries.ITEM.stream().filter(this::allowAsBase));
	}

	private boolean allowAsBase(Item item) {
		HolderSet<Item> blacklisted = getTag(TrimicaTags.SMITHING_BASE_BLACKLIST);
		if (blacklisted.contains(item.builtInRegistryHolder())) return false;

		HolderSet<Item> allTrimmables = getTag(TrimicaTags.ALL_TRIMMABLES);
		if (allTrimmables.contains(item.builtInRegistryHolder())) return true;
		if (!Platform.isModLoaded("elytratrims") && item == Items.ELYTRA) return false;

		DataComponentMap components = item.components();
		Equippable equippable = components.get(DataComponents.EQUIPPABLE);
		if (equippable == null) return false;

		return equippable.slot().getType() == EquipmentSlot.Type.HUMANOID_ARMOR;
	}
}
