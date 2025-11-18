package com.bawnorton.trimica.trim;

import com.bawnorton.trimica.data.tags.ConventionalTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

import java.util.Locale;
import java.util.Optional;

public enum TrimmedType {
	HELMET,
	CHESTPLATE,
	LEGGINGS,
	BOOTS,
	SHIELD,
	UNKNOWN;

	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}

	public boolean isOfArmour() {
		return this == HELMET || this == CHESTPLATE || this == LEGGINGS || this == BOOTS;
	}

	public static TrimmedType of(ItemStack stack) {
		EquipmentSlot slot = Optional.ofNullable(stack.get(DataComponents.EQUIPPABLE))
				.map(Equippable::slot)
				.orElse(null);
		return switch (slot) {
			case FEET -> TrimmedType.BOOTS;
			case LEGS -> TrimmedType.LEGGINGS;
			case CHEST -> TrimmedType.CHESTPLATE;
			case HEAD -> TrimmedType.HELMET;
			case null, default -> {
				if (stack.is(ConventionalTags.SHIELD_TOOLS)) {
					yield TrimmedType.SHIELD;
				} else {
					yield TrimmedType.UNKNOWN;
				}
			}
		};
	}
}