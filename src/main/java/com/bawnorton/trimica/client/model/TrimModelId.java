package com.bawnorton.trimica.client.model;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.trim.TrimmedType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import org.jetbrains.annotations.Nullable;

public record TrimModelId(
		TrimmedType type,
		ResourceLocation patternId,
		ResourceLocation materialId,
		@Nullable ResourceLocation assetId
) {
	public ResourceLocation asSingle() {
		String assetPrefix = assetId == null ? "" : "%s/".formatted(assetId.toString().replace(":", "/"));
		assetPrefix += type.getName();
		return Trimica.rl("%s/%s/%s/%s".formatted(assetPrefix, patternId.getNamespace(), patternId.getPath(), materialId.getPath()));
	}

	public static TrimModelId fromTrim(TrimmedType trimmedType, ArmorTrim trim, @Nullable ResourceKey<EquipmentAsset> assetKey) {
		ResourceLocation materialId = Trimica.rl(Trimica.getMaterialRegistry().getSuffix(trim.material().value(), assetKey));
		ResourceLocation patternId = trim.pattern().value().assetId();
		return new TrimModelId(trimmedType, patternId, materialId, assetKey == null ? null : assetKey.location());
	}
}