package com.bawnorton.trimica.client.model;

import com.bawnorton.trimica.Trimica;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record TrimModelId(String equipment, ResourceLocation patternId, ResourceLocation materialId, @Nullable ResourceLocation assetId) {
    public ResourceLocation asSingle() {
        String assetPrefix = assetId == null ? "" : "%s/".formatted(assetId.toString().replace(":", "/"));
        assetPrefix += equipment;
        return Trimica.rl("%s/%s/%s/%s".formatted(assetPrefix, patternId.getNamespace(), patternId.getPath(), materialId.getPath()));
    }

    public static TrimModelId fromTrim(String equipment, ArmorTrim trim, @Nullable ResourceKey<EquipmentAsset> assetKey) {
        MaterialAssetGroup assets = trim.material().value().assets();
        MaterialAssetGroup.AssetInfo assetInfo = assetKey == null ? assets.base() : assets.assetId(assetKey);
        ResourceLocation materialId = Trimica.rl(assetInfo.suffix());
        ResourceLocation patternId = trim.pattern().value().assetId();
        if (assetKey != null) {
            return new TrimModelId(equipment, patternId, materialId, assetKey.location());
        }
        return new TrimModelId(equipment, patternId, materialId, null);
    }
}