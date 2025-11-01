package com.bawnorton.trimica.item.component;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.trim.TrimMaterialRuntimeRegistry;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ProvidesTrimMaterial;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public final class ComponentUtil {
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
		if (original != null) return original;

		if (type == DataComponents.PROVIDES_TRIM_MATERIAL && TrimMaterialRuntimeRegistry.enableTrimEverything) {
			return (T) new ProvidesTrimMaterial(Trimica.getMaterialRegistry().getOrCreate(instance));
		}
		return null;
	}

	public static <T> void setFakeComponents(DataComponentSetter setter, DataComponentGetter getter, DataComponentType<T> type, @Nullable T object) {
		if (!MaterialAdditions.enableMaterialAdditions) return;
		if (object == null) return;

		if (type == DataComponents.TRIM) {
			ArmorTrim trim = (ArmorTrim) object;
			MaterialAdditions additions = getter.getOrDefault(MaterialAdditions.TYPE, MaterialAdditions.NONE);
			TrimMaterial trimMaterial = trim.material().value();
			additions = additions.and(Trimica.getMaterialRegistry().getIntrinsicAdditions(trimMaterial));
			if (additions.isEmpty()) return;

			setter.set(MaterialAdditions.TYPE, additions);
		} else if (AdditionalTrims.enableAdditionalTrims && type == AdditionalTrims.TYPE) {
			AdditionalTrims additionalTrims = (AdditionalTrims) object;
			MaterialAdditions additions = getter.getOrDefault(MaterialAdditions.TYPE, MaterialAdditions.NONE);
			for (ArmorTrim trim : additionalTrims.trims()) {
				TrimMaterial trimMaterial = trim.material().value();
				additions = additions.and(Trimica.getMaterialRegistry().getIntrinsicAdditions(trimMaterial));
			}
			if (additions.isEmpty()) return;

			setter.set(MaterialAdditions.TYPE, additions);
		}
	}
}
