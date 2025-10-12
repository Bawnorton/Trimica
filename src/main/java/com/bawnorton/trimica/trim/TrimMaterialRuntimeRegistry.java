package com.bawnorton.trimica.trim;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class TrimMaterialRuntimeRegistry {
	private final Map<String, MaterialAdditions> intrinsicAdditions = new HashMap<>();
	private final Map<String, Item> materialProviders = new HashMap<>();
	private final Map<ResourceLocation, Holder<TrimMaterial>> materials = new HashMap<>();

	/**
	 * Whether you can trim anything with anything.
	 * Disabling this will prevent non-builtin materials from being used for trimming
	 * and prevent any armour that doesn't support trimming from being trimmed
	 */
	@Configurable(onSet = "com.bawnorton.trimica.Trimica#refreshEverything")
	public static boolean enableTrimEverything = true;

	public Item getMaterialProvider(String suffix) {
		return materialProviders.computeIfAbsent(suffix, k -> {
			int trimicaStartIndex = k.indexOf("trimica/");
			if (trimicaStartIndex == -1) {
				return null;
			}
			String materialId = k.substring(trimicaStartIndex + "trimica/".length()).replace("-", ":");
			ResourceLocation id = ResourceLocation.tryParse(materialId);
			if (id == null) {
				return null;
			}
			return BuiltInRegistries.ITEM.getOptional(id).orElse(null);
		});
	}

	public Holder<TrimMaterial> getOrCreate(ItemStack stack) {
		ResourceLocation itemKey = stack.getOrDefault(DataComponents.ITEM_MODEL, BuiltInRegistries.ITEM.getKey(stack.getItem()));
		ResourceLocation materialKey = Trimica.rl("generated/%s/%s".formatted(itemKey.getNamespace(), itemKey.getPath()));
		return materials.computeIfAbsent(materialKey, k -> {
			String suffix = "trimica/%s-%s".formatted(itemKey.getNamespace(), itemKey.getPath());
			MaterialAssetGroup id = MaterialAssetGroup.create(suffix);
			materialProviders.computeIfAbsent(suffix, material -> stack.getItem());
			return Holder.direct(new TrimMaterial(id, Component.translatable("trimica.material", stack.getHoverName().getString())));
		});
	}

	public void registerMaterialReference(Holder.Reference<TrimMaterial> reference) {
		materials.put(reference.key().location(), reference);
	}

	public void setIntrinsicAdditions(TrimMaterial trimMaterial, MaterialAdditions addition) {
		if (addition == null) {
			return;
		}
		intrinsicAdditions.put(trimMaterial.assets().base().suffix(), addition);
	}

	public @Nullable MaterialAdditions getIntrinsicAdditions(TrimMaterial trimMaterial) {
		return intrinsicAdditions.get(trimMaterial.assets().base().suffix());
	}
}
