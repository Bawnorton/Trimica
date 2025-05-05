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

public final class RuntimeTrimMaterials {
    private static final Map<ResourceLocation, TrimMaterial> MATERIALS = new HashMap<>();
    private static final Map<MaterialAssetGroup, Item> MATERIAL_PROVIDERS = new HashMap<>();

    public static TrimMaterial getOrCreate(ItemStack stack) {
        ResourceLocation model = stack.getOrDefault(DataComponents.ITEM_MODEL, BuiltInRegistries.ITEM.getKey(stack.getItem()));
        return MATERIALS.computeIfAbsent(model, key -> {
            MaterialAssetGroup id = MaterialAssetGroup.create("/%s-%s".formatted(key.getNamespace(), key.getPath()));
            TrimMaterial runtimeMaterial = new TrimMaterial(id, Component.translatable("trimica.material", stack.getHoverName().getString()));
            MATERIAL_PROVIDERS.computeIfAbsent(id, material -> stack.getItem());
            return runtimeMaterial;
        });
    }

    public static Item getMaterialProvider(TrimMaterial material) {
        return MATERIAL_PROVIDERS.get(material.assets());
    }
}
