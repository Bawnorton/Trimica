package com.bawnorton.trimica.client.palette;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.api.impl.TrimicaApiImpl;
import com.bawnorton.trimica.item.component.MaterialAddition;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class TrimPalettes {
    private final ConcurrentMap<ResourceLocation, TrimPalette> cache = new ConcurrentHashMap<>();
    private final TrimPaletteGenerator generator = new TrimPaletteGenerator();

    public TrimPalette getOrGeneratePalette(TrimMaterial material, ResourceKey<EquipmentAsset> equipmentAssetKey, ResourceLocation location, @Nullable DataComponentGetter componentGetter) {
        String suffix = equipmentAssetKey == null ? material.assets().base().suffix() : material.assets().assetId(equipmentAssetKey).suffix();
        ResourceLocation key = Trimica.rl(suffix);
        MaterialAddition addition;
        if (componentGetter == null) {
            addition = null;
        } else {
            addition = componentGetter.get(MaterialAddition.TYPE);
            if(addition != null) {
                key = addition.apply(key);
            }
        }
        return cache.computeIfAbsent(key, k -> {
            TrimPalette palette = generator.generatePalette(material, suffix, location);
            if (Trimica.getMaterialRegistry().getIsAnimated(material)) {
                palette = palette.asAnimated();
            } else if (addition != null) {
                palette = palette.withMaterialAddition(addition);
            }
            return TrimicaApiImpl.INSTANCE.applyPaletteInterceptors(palette, material);
        });
    }

    public @Nullable TrimPalette getPalette(TrimMaterial material, ResourceKey<EquipmentAsset> equipmentAssetKey, @Nullable DataComponentGetter componentGetter) {
        String suffix = equipmentAssetKey == null ? material.assets().base().suffix() : material.assets().assetId(equipmentAssetKey).suffix();
        ResourceLocation key = Trimica.rl(suffix);
        if (componentGetter != null) {
            MaterialAddition addition = componentGetter.get(MaterialAddition.TYPE);
            if(addition != null) {
                key = addition.apply(key);
            }
        }
        return cache.get(key);
    }
}