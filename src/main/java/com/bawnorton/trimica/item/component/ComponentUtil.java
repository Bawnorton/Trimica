package com.bawnorton.trimica.item.component;

import com.bawnorton.trimica.Trimica;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ProvidesTrimMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
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

    @SuppressWarnings("unchecked")
    public static <T> @Nullable T getFakeComponents(DataComponentGetter instance, T original, DataComponentType<? extends T> type) {
        if(original != null) return original;

        if (type == DataComponents.PROVIDES_TRIM_MATERIAL && instance instanceof ItemStack stack) {
            return (T) new ProvidesTrimMaterial(Holder.direct(Trimica.getMaterialRegistry().getOrCreate(stack)));
        }
        return null;
    }

    public static <T> void setFakeComponents(DataComponentSetter setter, DataComponentGetter getter, DataComponentType<T> type, @Nullable T object) {
        if(type == DataComponents.TRIM && object != null) {
            ArmorTrim trim = (ArmorTrim) object;
            MaterialAdditions additions = getter.get(MaterialAdditions.TYPE);
            if (additions == null) {
                TrimMaterial trimMaterial = trim.material().value();
                additions = Trimica.getMaterialRegistry().getIntrinsicAdditions(trimMaterial);
                if (additions == null || additions.additionKeys().isEmpty()) {
                    return;
                }
            }
            setter.set(MaterialAdditions.TYPE, additions);
        }
    }
}
