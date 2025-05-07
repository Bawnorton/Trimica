package com.bawnorton.trimica.trim.material;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import java.util.HashMap;
import java.util.Map;

public final class TrimMaterialRuntimeRegistry {
    private final Map<MaterialAssetGroup, Item> materialProviders = new HashMap<>();
    private final Map<ResourceLocation, TrimMaterial> materials = new HashMap<>();

    public Item getMaterialProvider(TrimMaterial material) {
        return materialProviders.computeIfAbsent(material.assets(), k -> {
            String trimicaId = k.base().suffix();
            int trimicaStartIndex = trimicaId.indexOf("trimica/");
            if (trimicaStartIndex == -1) {
                return null;
            }
            String materialId = trimicaId.substring(trimicaStartIndex + "trimica/".length()).replace("-", ":");
            ResourceLocation id = ResourceLocation.tryParse(materialId);
            if (id == null) {
                return null;
            }
            return BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        });
    }

    public TrimMaterial getOrCreate(ItemStack stack) {
        ResourceLocation key = stack.getOrDefault(DataComponents.ITEM_MODEL, BuiltInRegistries.ITEM.getKey(stack.getItem()));
        return materials.computeIfAbsent(key, k -> {
            MaterialAssetGroup id = MaterialAssetGroup.create("trimica/%s-%s".formatted(key.getNamespace(), key.getPath()));
            materialProviders.computeIfAbsent(id, material -> stack.getItem());
            return new TrimMaterial(id, Component.translatable("trimica.material", stack.getHoverName().getString()));
        });
    }
}
