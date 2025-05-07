package com.bawnorton.trimica.client.texture.palette;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class TrimPalettes {
    private final ConcurrentMap<MaterialAssetGroup, TrimPalette> cache = new ConcurrentHashMap<>();
    private final TrimPaletteGenerator generator = new TrimPaletteGenerator();

    public TrimPalette getOrGeneratePalette(TrimMaterial material, ResourceLocation location) {
        return cache.computeIfAbsent(material.assets(), k -> generator.generatePalette(material, location));
    }

    public @Nullable TrimPalette getPalette(TrimMaterial material) {
        return cache.get(material.assets());
    }
}