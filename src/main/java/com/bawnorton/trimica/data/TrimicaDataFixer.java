package com.bawnorton.trimica.data;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

import java.util.Map;

public final class TrimicaDataFixer {
	public Holder<TrimMaterial> fixDynamicTrimMaterialSuffix(Holder<TrimMaterial> material) {
		if (material instanceof Holder.Direct<TrimMaterial>(TrimMaterial value)) {
			TrimMaterial fixedMaterial = fixDynamicTrimMaterialSuffix(value);
			return Holder.direct(fixedMaterial);
		}
		return material;
	}

	private static TrimMaterial fixDynamicTrimMaterialSuffix(TrimMaterial value) {
		MaterialAssetGroup assets = value.assets();
		MaterialAssetGroup.AssetInfo base = assets.base();
		Map<ResourceKey<EquipmentAsset>, MaterialAssetGroup.AssetInfo> overrides = assets.overrides();

		MaterialAssetGroup.AssetInfo fixed = fixAssetInfo(base);
		for (Map.Entry<ResourceKey<EquipmentAsset>, MaterialAssetGroup.AssetInfo> entry : overrides.entrySet()) {
			MaterialAssetGroup.AssetInfo fixedOverride = fixAssetInfo(entry.getValue());
			if (fixedOverride != entry.getValue()) {
				overrides.put(entry.getKey(), fixedOverride);
			}
		}
		if (fixed != base || !overrides.equals(assets.overrides())) {
			MaterialAssetGroup fixedAssets = new MaterialAssetGroup(fixed, overrides);
			return new TrimMaterial(fixedAssets, value.description());
		}
		return value;
	}

	private static MaterialAssetGroup.AssetInfo fixAssetInfo(MaterialAssetGroup.AssetInfo base) {
		String existingSuffix = base.suffix();
		int trimicaStartIndex = existingSuffix.indexOf("trimica/");
		if (trimicaStartIndex == -1) {
			return base;
		}
		String dynamicComponent = existingSuffix.substring(trimicaStartIndex + "trimica/".length());
		/*
		 if the dynamic component is of the pattern `namespace-path` convert it to `namespace/path`, however
		 `namespace/path-more` is valid and should not be changed, and so we only replace the first `-` with `/`
		 if it is before any `/` character
		*/
		int slashIndex = dynamicComponent.indexOf('/');
		int dashIndex = dynamicComponent.indexOf('-');
		if (dashIndex != -1 && (slashIndex == -1 || dashIndex < slashIndex)) {
			String fixedComponent = dynamicComponent.replaceFirst("-", "/");
			String fixedSuffix = "trimica/" + fixedComponent;
			return new MaterialAssetGroup.AssetInfo(fixedSuffix);
		}
		return base;
	}
}
