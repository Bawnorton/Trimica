package com.bawnorton.trimica.client.model;

import com.bawnorton.trimica.Trimica;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import org.jetbrains.annotations.Nullable;

public record TrimModelId(String equipment, ResourceLocation materialId) {
    public ResourceLocation asSingle() {
        return Trimica.rl("%s/%s".formatted(equipment, materialId.getPath()));
    }

    public static TrimModelId fromTrim(String equipment, ArmorTrim trim, @Nullable ResourceKey<EquipmentAsset> assetKey) {
        MaterialAssetGroup assets = trim.material().value().assets();
        MaterialAssetGroup.AssetInfo assetInfo = assetKey == null ? assets.base() : assets.assetId(assetKey);
        ResourceLocation materialId = Trimica.rl(assetInfo.suffix());
        return new TrimModelId(equipment, materialId);
    }
}