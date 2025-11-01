package com.bawnorton.trimica.crafting;

import com.bawnorton.trimica.api.CraftingRecipeInterceptor;
import com.bawnorton.trimica.data.tags.TrimicaTags;
import com.bawnorton.trimica.mixin.accessor.IngredientAccessor;
import com.bawnorton.trimica.platform.Platform;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.equipment.Equippable;

public class DefaultCraftingRecipeInterceptor implements CraftingRecipeInterceptor {
	private final Object2BooleanArrayMap<Item> allowedAsArmourBase = new Object2BooleanArrayMap<>(BuiltInRegistries.ITEM.size());
	private final Object2BooleanArrayMap<Item> allowedAsToolBase = new Object2BooleanArrayMap<>(BuiltInRegistries.ITEM.size());

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
		HolderSet<Item> holders = ((IngredientAccessor) (Object) current).trimica$getValues();
		if(holders instanceof HolderSet.Named<Item> named) {
			TagKey<Item> key = named.key();
			if(key.equals(TrimicaTags.ALL_TRIMMABLES)) {
				return Ingredient.of(BuiltInRegistries.ITEM.stream().filter(item -> this.armourAsBase(item) || this.toolsAsBase(item)));
			} else if(key.equals(TrimicaTags.TRIMMABLE_TOOLS)) {
				return Ingredient.of(BuiltInRegistries.ITEM.stream().filter(this::toolsAsBase));
			} else if (key.equals(ItemTags.TRIMMABLE_ARMOR)) {
				return Ingredient.of(BuiltInRegistries.ITEM.stream().filter(this::armourAsBase));
			}
		}
		return current;
	}

	private boolean armourAsBase(Item item) {
		return allowedAsArmourBase.computeIfAbsent(item, k -> {
			HolderSet<Item> blacklisted = getTag(TrimicaTags.SMITHING_BASE_BLACKLIST);
			if (blacklisted.contains(item.builtInRegistryHolder())) return false;

			DataComponentMap components = item.components();
			Equippable equippable = components.get(DataComponents.EQUIPPABLE);
			if (equippable == null) return false;

			if (!Platform.isModLoaded("elytratrims") && item == Items.ELYTRA) return false;

			EquipmentSlot slot = equippable.slot();
			return slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR || item == Items.SHIELD;
		});
	}

	private boolean toolsAsBase(Item item) {
		return allowedAsToolBase.computeIfAbsent(item, k -> {
			HolderSet<Item> blacklisted = getTag(TrimicaTags.SMITHING_BASE_BLACKLIST);
			if (blacklisted.contains(item.builtInRegistryHolder())) return false;

			DataComponentMap components = item.components();
			return components.has(DataComponents.TOOL);
		});
	}
}
