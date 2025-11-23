package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.model.TrimModelId;
import com.bawnorton.trimica.item.component.AdditionalTrims;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.bawnorton.trimica.trim.TrimmedType;
import com.bawnorton.trimica.util.Lazy;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class RuntimeTrimAtlases {
	private final Map<TrimMaterial, Map<EquipmentClientInfo.LayerType, Lazy<RuntimeTrimAtlas>>> equipmentAtlases = new HashMap<>();
	private final Map<TrimMaterial, Lazy<RuntimeTrimAtlas>> itemAtlases = new HashMap<>();
	private final Map<TrimMaterial, Lazy<RuntimeTrimAtlas>> shieldAtlases = new HashMap<>();

	private final List<Consumer<RuntimeTrimAtlas>> modelAtlasModifiedListeners = new ArrayList<>();

	private boolean shouldResetUVs = false;

	public void init(RegistryAccess registryAccess) {
		equipmentAtlases.values().forEach(Map::clear);
		equipmentAtlases.clear();
		itemAtlases.clear();
		shieldAtlases.clear();

		Registry<TrimMaterial> materials = registryAccess.lookup(Registries.TRIM_MATERIAL).orElseThrow();

		for (TrimMaterial material : materials) {
			for (EquipmentClientInfo.LayerType layerType : EquipmentClientInfo.LayerType.values()) {
				createEquipmentAtlas(registryAccess, material, layerType);
			}
			createItemAtlas(registryAccess, material);
			createShieldAtlas(registryAccess, material);
		}
	}

	private void resetFrames() {
		for (Map<EquipmentClientInfo.LayerType, Lazy<RuntimeTrimAtlas>> layerBasedAtlases : equipmentAtlases.values()) {
			for (Lazy<RuntimeTrimAtlas> atlas : layerBasedAtlases.values()) {
				if (atlas.isPresent()) {
					atlas.get().resetFrames();
				}
			}
		}
	}

	private Lazy<RuntimeTrimAtlas> createEquipmentAtlas(RegistryAccess registryAccess, TrimMaterial material, EquipmentClientInfo.LayerType layerType) {
		Registry<TrimMaterial> materials = registryAccess.lookup(Registries.TRIM_MATERIAL).orElseThrow();
		Registry<TrimPattern> patterns = registryAccess.lookup(Registries.TRIM_PATTERN).orElseThrow();
		String materialId = Trimica.getMaterialRegistry().getSuffix(material);
		return new Lazy<>(() -> new RuntimeTrimAtlas(
				Trimica.rl("%s/%s.png".formatted(materialId, layerType.getSerializedName())),
				new TrimArmourSpriteFactory(layerType),
				(p) -> new ArmorTrim(materials.wrapAsHolder(material), patterns.wrapAsHolder(p)),
				atlas -> {
					resetFrames();
					for (Consumer<RuntimeTrimAtlas> listener : modelAtlasModifiedListeners) {
						listener.accept(atlas);
					}
				}
		));
	}

	private Lazy<RuntimeTrimAtlas> createItemAtlas(RegistryAccess registryAccess, TrimMaterial material) {
		Registry<TrimMaterial> materials = registryAccess.lookup(Registries.TRIM_MATERIAL).orElseThrow();
		Registry<TrimPattern> patterns = registryAccess.lookup(Registries.TRIM_PATTERN).orElseThrow();
		String materialId = Trimica.getMaterialRegistry().getSuffix(material);
		return new Lazy<>(() -> new RuntimeTrimAtlas(
				Trimica.rl("%s/item.png".formatted(materialId)),
				new TrimItemSpriteFactory(),
				(p) -> new ArmorTrim(materials.wrapAsHolder(material), patterns.wrapAsHolder(p)),
				atlas -> {
					TrimicaClient.getItemModelFactory().clearModels();
					for (Consumer<RuntimeTrimAtlas> listener : modelAtlasModifiedListeners) {
						listener.accept(atlas);
					}
				}
		));
	}

	private Lazy<RuntimeTrimAtlas> createShieldAtlas(RegistryAccess registryAccess, TrimMaterial material) {
		Registry<TrimMaterial> materials = registryAccess.lookup(Registries.TRIM_MATERIAL).orElseThrow();
		Registry<TrimPattern> patterns = registryAccess.lookup(Registries.TRIM_PATTERN).orElseThrow();
		String materialId = Trimica.getMaterialRegistry().getSuffix(material);
		return new Lazy<>(() -> new RuntimeTrimAtlas(
				Trimica.rl("%s/shield.png".formatted(materialId)),
				new TrimShieldSpriteFactory(),
				(p) -> new ArmorTrim(materials.wrapAsHolder(material), patterns.wrapAsHolder(p)),
				atlas -> {
					for (Consumer<RuntimeTrimAtlas> listener : modelAtlasModifiedListeners) {
						listener.accept(atlas);
					}
				}
		));
	}

	public RuntimeTrimAtlas getEquipmentAtlas(ClientLevel level, TrimMaterial material, EquipmentClientInfo.LayerType layerType) {
		return equipmentAtlases.computeIfAbsent(material, k -> new HashMap<>())
				.computeIfAbsent(layerType, k -> createEquipmentAtlas(level.registryAccess(), material, layerType))
				.get();
	}

	public RuntimeTrimAtlas getItemAtlas(ClientLevel level, TrimMaterial material) {
		return itemAtlases.computeIfAbsent(material, k -> createItemAtlas(level.registryAccess(), k)).get();
	}

	public RuntimeTrimAtlas getShieldAtlas(ClientLevel level, TrimMaterial material) {
		return shieldAtlases.computeIfAbsent(material, k -> createShieldAtlas(level.registryAccess(), k)).get();
	}

	public List<DynamicTrimTextureAtlasSprite> getShieldSprites(ClientLevel level, DataComponentGetter getter) {
		List<DynamicTrimTextureAtlasSprite> sprites = new ArrayList<>();
		List<ArmorTrim> trims = AdditionalTrims.getAllTrims(getter);
		for (ArmorTrim trim : trims) {

			TrimModelId trimModelId = TrimModelId.fromTrim(TrimmedType.SHIELD, trim, null);
			ResourceLocation overlayLocation = trimModelId.asSingle();
			if (MaterialAdditions.enableMaterialAdditions) {
				MaterialAdditions addition = getter.getOrDefault(MaterialAdditions.TYPE, MaterialAdditions.NONE);
				overlayLocation = addition.apply(overlayLocation);
			}
			sprites.add(getShieldAtlas(level, trim.material().value()).getSprite(getter, trim.pattern().value(), overlayLocation));
		}
		return sprites;
	}

	public void addModelAtlasModifiedListener(Consumer<RuntimeTrimAtlas> listener) {
		modelAtlasModifiedListeners.add(listener);
	}

	public boolean shouldResetUVs() {
		return shouldResetUVs;
	}

	public void setShouldResetUVs(boolean shouldResetUVs) {
		this.shouldResetUVs = shouldResetUVs;
	}


	public void clear() {
		equipmentAtlases.forEach((pattern, lazyMap) ->
				lazyMap.forEach((layer, lazy) -> lazy.ifPresent(RuntimeTrimAtlas::clear))
		);
		itemAtlases.forEach((pattern, lazy) -> lazy.ifPresent(RuntimeTrimAtlas::clear));
		shieldAtlases.forEach((pattern, lazy) -> lazy.ifPresent(RuntimeTrimAtlas::clear));
		equipmentAtlases.clear();
		itemAtlases.clear();
		shieldAtlases.clear();
	}

	public interface TrimFactory {
		ArmorTrim create(TrimPattern pattern);
	}
}