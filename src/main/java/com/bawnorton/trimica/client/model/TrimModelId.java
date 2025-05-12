package com.bawnorton.trimica.client.model;

import com.bawnorton.trimica.Trimica;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import org.jetbrains.annotations.Nullable;

public record TrimModelId(ResourceLocation modelId, ResourceLocation materialId, ResourceLocation patternId) {
    public ResourceLocation asSingle() {
        return ResourceLocation.fromNamespaceAndPath(modelId.getNamespace(), "%s/%s_%s".formatted(modelId.getPath(), patternId.getPath(), materialId.getPath()));
    }

    public static TrimModelId fromTrim(DataComponentGetter dataComponentGetter, ArmorTrim trim, Item fallbackItem, @Nullable ResourceKey<EquipmentAsset> assetKey) {
        ResourceLocation modelId = dataComponentGetter.getOrDefault(DataComponents.ITEM_MODEL, BuiltInRegistries.ITEM.getKey(fallbackItem));
        MaterialAssetGroup assets = trim.material().value().assets();
        MaterialAssetGroup.AssetInfo assetInfo = assetKey == null ? assets.base() : assets.assetId(assetKey);
        ResourceLocation materialId = Trimica.rl(assetInfo.suffix());
        ResourceLocation patternId = trim.pattern().value().assetId();
        return new TrimModelId(modelId, materialId, patternId);
    }
}