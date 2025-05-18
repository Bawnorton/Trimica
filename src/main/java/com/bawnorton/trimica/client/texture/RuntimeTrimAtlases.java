package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.TrimicaClient;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
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
    private final Map<TrimPattern, Map<EquipmentClientInfo.LayerType, RuntimeTrimAtlas>> equipmentAtlases = new HashMap<>();
    private final Map<TrimPattern, RuntimeTrimAtlas> itemAtlases = new HashMap<>();
    private final Map<TrimPattern, RuntimeTrimAtlas> shieldAtlases = new HashMap<>();

    private final List<Consumer<RuntimeTrimAtlas>> modelAtlasModifiedListeners = new ArrayList<>();

    public void init(RegistryAccess registryAccess) {
        equipmentAtlases.values().forEach(Map::clear);
        equipmentAtlases.clear();
        itemAtlases.clear();
        shieldAtlases.clear();

        Registry<TrimPattern> patterns = registryAccess.lookup(Registries.TRIM_PATTERN).orElseThrow();
        Registry<TrimMaterial> materials = registryAccess.lookup(Registries.TRIM_MATERIAL).orElseThrow();

        for (TrimPattern pattern : patterns) {
            ResourceLocation patternId = pattern.assetId();
            Map<EquipmentClientInfo.LayerType, RuntimeTrimAtlas> atlases = new HashMap<>();
            for (EquipmentClientInfo.LayerType layerType : List.of(EquipmentClientInfo.LayerType.HUMANOID, EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS)) {
                atlases.put(layerType, new RuntimeTrimAtlas(
                        Trimica.rl("%s/%s/%s.png".formatted(patternId.getNamespace(), patternId.getPath(), layerType.getSerializedName())),
                        new TrimArmourSpriteFactory(layerType),
                        (m) -> new ArmorTrim(materials.wrapAsHolder(m), patterns.wrapAsHolder(pattern)),
                        atlas -> {
                            resetFrames();
                            for (Consumer<RuntimeTrimAtlas> listener : modelAtlasModifiedListeners) {
                                listener.accept(atlas);
                            }
                        }
                ));
            }
            equipmentAtlases.put(pattern, atlases);
            itemAtlases.put(
                    pattern, new RuntimeTrimAtlas(
                            Trimica.rl("%s/%s/item.png".formatted(patternId.getNamespace(), patternId.getPath())),
                            new TrimItemSpriteFactory(),
                            (m) -> new ArmorTrim(materials.wrapAsHolder(m), patterns.wrapAsHolder(pattern)),
                            atlas -> TrimicaClient.getItemModelFactory().clear()
                    )
            );
            shieldAtlases.put(
                    pattern, new RuntimeTrimAtlas(
                            Trimica.rl("%s/%s/shield.png".formatted(patternId.getNamespace(), patternId.getPath())),
                            new TrimShieldSpriteFactory(),
                            (m) -> new ArmorTrim(materials.wrapAsHolder(m), patterns.wrapAsHolder(pattern)),
                            atlas -> {}
                    )
            );
        }
    }

    private void resetFrames() {
        for(Map<EquipmentClientInfo.LayerType, RuntimeTrimAtlas> layerBasedAtlases : equipmentAtlases.values()) {
            for(RuntimeTrimAtlas atlas : layerBasedAtlases.values()) {
                atlas.resetFrames();
            }
        }
    }

    public RuntimeTrimAtlas getEquipmentAtlas(TrimPattern pattern, EquipmentClientInfo.LayerType layerType) {
        return equipmentAtlases.get(pattern).get(layerType);
    }

    public RuntimeTrimAtlas getItemAtlas(TrimPattern pattern) {
        return itemAtlases.get(pattern);
    }

    public RuntimeTrimAtlas getShieldAtlas(TrimPattern pattern) {
        return shieldAtlases.get(pattern);
    }

    public void registerModelAtlasModifiedListener(Consumer<RuntimeTrimAtlas> consumer) {
        modelAtlasModifiedListeners.add(consumer);
    }

    public interface TrimFactory {
        ArmorTrim create(TrimMaterial material);
    }
}
