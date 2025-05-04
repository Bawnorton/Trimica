package com.bawnorton.trimica.trim.material;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import java.util.HashMap;
import java.util.Map;

public final class RuntimeTrimMaterials {
    private final static Map<ResourceLocation, TrimMaterial> MATERIALS = new HashMap<>();

    public static TrimMaterial getOrCreate(Item item) {
        ResourceLocation rl = BuiltInRegistries.ITEM.getKey(item);
        return MATERIALS.computeIfAbsent(rl, key -> new TrimMaterial(MaterialAssetGroup.create(key.getPath()), Component.translatable("trimica.material", item.getName().getString())));
    }

    public static boolean containsMaterial(Item item) {
        ResourceLocation rl = BuiltInRegistries.ITEM.getKey(item);
        return MATERIALS.containsKey(rl);
    }
}
