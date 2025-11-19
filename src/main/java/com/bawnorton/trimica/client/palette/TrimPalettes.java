package com.bawnorton.trimica.client.palette;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.api.client.impl.TrimicaClientApiImpl;
import com.bawnorton.trimica.api.impl.TrimicaApiImpl;
import com.bawnorton.trimica.item.component.MaterialAdditions;
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

	@SuppressWarnings("deprecation")
	public TrimPalette getOrGeneratePalette(TrimMaterial material, @Nullable ResourceKey<EquipmentAsset> assetKey, @Nullable DataComponentGetter componentGetter) {
		ResourceLocation key = Trimica.rl(Trimica.getMaterialRegistry().getSuffix(material, assetKey));
		MaterialAdditions additions;
		if (componentGetter == null || !MaterialAdditions.enableMaterialAdditions) {
			additions = MaterialAdditions.NONE;
		} else {
			additions = Trimica.getMaterialRegistry().getIntrinsicAdditions(material)
					.and(componentGetter.get(MaterialAdditions.TYPE));
			key = additions.apply(key);
		}
		return cache.computeIfAbsent(key, k -> {
			TrimPalette palette = generator.generatePalette(material, assetKey);
			// TrimicaApiImpl method deprecated for removal in 2.0.0
			palette = TrimicaApiImpl.INSTANCE.applyPaletteInterceptorsForMaterialAdditions(palette, additions);
			palette = TrimicaApiImpl.INSTANCE.applyPaletteInterceptorsForGeneration(palette, material);
			return TrimicaClientApiImpl.INSTANCE.applyPaletteInterceptorsForGeneration(palette, material, additions);
		});
	}

	public @Nullable TrimPalette getPalette(TrimMaterial material, ResourceKey<EquipmentAsset> assetKey, @Nullable DataComponentGetter componentGetter) {
		String suffix = Trimica.getMaterialRegistry().getSuffix(material, assetKey);
		ResourceLocation key = Trimica.rl(suffix);
		if (componentGetter != null && MaterialAdditions.enableMaterialAdditions) {
			MaterialAdditions addition = componentGetter.getOrDefault(MaterialAdditions.TYPE, MaterialAdditions.NONE);
			key = addition.apply(key);
		}
		return cache.get(key);
	}

	public void clear() {
		cache.clear();
		generator.clear();
	}
}