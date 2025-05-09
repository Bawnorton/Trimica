package com.bawnorton.trimica.client.texture.palette;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class TrimPalettes {
    private final ConcurrentMap<String, TrimPalette> cache = new ConcurrentHashMap<>();
    private final TrimPaletteGenerator generator = new TrimPaletteGenerator();

    public TrimPalette getOrGeneratePalette(TrimMaterial material, @Nullable ResourceKey<EquipmentAsset> equipmentAssetKey, ResourceLocation location) {
        String suffix = equipmentAssetKey == null ? material.assets().base().suffix() : material.assets().assetId(equipmentAssetKey).suffix();
        return cache.computeIfAbsent(suffix, k -> generator.generatePalette(material, k, location));
    }

    public @Nullable TrimPalette getPalette(TrimMaterial material, ResourceKey<EquipmentAsset> equipmentAssetKey) {
        return cache.get(material.assets().assetId(equipmentAssetKey).suffix());
    }
}