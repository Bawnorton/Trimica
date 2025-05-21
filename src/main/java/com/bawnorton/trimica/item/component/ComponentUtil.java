package com.bawnorton.trimica.item.component;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.stream.Stream;

public final class ComponentUtil {
    public static @Nullable ArmorType getArmourType(DataComponentGetter componentGetter) {
        Equippable equippable = componentGetter.get(DataComponents.EQUIPPABLE);
        if (equippable == null) return null;

        EquipmentSlot slot = equippable.slot();
        return switch (slot) {
            case FEET -> ArmorType.BOOTS;
            case LEGS -> ArmorType.LEGGINGS;
            case CHEST -> ArmorType.CHESTPLATE;
            case HEAD -> ArmorType.HELMET;
            default -> null;
        };
    }

    public static @NotNull List<ItemStack> getTrimmedEquipment(ArmorTrim trim) {
        return Stream.of(
                Items.DIAMOND_HELMET,
                Items.DIAMOND_CHESTPLATE,
                Items.DIAMOND_LEGGINGS,
                Items.DIAMOND_BOOTS,
                Items.SHIELD
        ).map(item -> {
            ItemStack stack = item.getDefaultInstance();
            stack.set(DataComponents.TRIM, trim);
            return stack;
        }).toList();
    }
}
