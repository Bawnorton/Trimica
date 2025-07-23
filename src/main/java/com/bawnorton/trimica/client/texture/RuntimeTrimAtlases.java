package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.model.TrimModelId;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.bawnorton.trimica.util.Lazy;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
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
import java.util.function.Supplier;

public final class RuntimeTrimAtlases {
    private final Map<TrimPattern, Map<EquipmentClientInfo.LayerType, Lazy<RuntimeTrimAtlas>>> equipmentAtlases = new HashMap<>();
    private final Map<TrimPattern, Lazy<RuntimeTrimAtlas>> itemAtlases = new HashMap<>();
    private final Map<TrimPattern, Lazy<RuntimeTrimAtlas>> shieldAtlases = new HashMap<>();

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
            Map<EquipmentClientInfo.LayerType, Lazy<RuntimeTrimAtlas>> atlases = new HashMap<>();
            for (EquipmentClientInfo.LayerType layerType : EquipmentClientInfo.LayerType.values()) {
                atlases.put(layerType, new Lazy<>(() -> new RuntimeTrimAtlas(
                        Trimica.rl("%s/%s/%s.png".formatted(patternId.getNamespace(), patternId.getPath(), layerType.getSerializedName())),
                        new TrimArmourSpriteFactory(layerType),
                        (m) -> new ArmorTrim(materials.wrapAsHolder(m), patterns.wrapAsHolder(pattern)),
                        atlas -> {
                            resetFrames();
                            for (Consumer<RuntimeTrimAtlas> listener : modelAtlasModifiedListeners) {
                                listener.accept(atlas);
                            }
                        }
                )));
            }
            equipmentAtlases.put(pattern, atlases);
            itemAtlases.put(
                    pattern, new Lazy<>(() -> new RuntimeTrimAtlas(
                            Trimica.rl("%s/%s/item.png".formatted(patternId.getNamespace(), patternId.getPath())),
                            new TrimItemSpriteFactory(),
                            (m) -> new ArmorTrim(materials.wrapAsHolder(m), patterns.wrapAsHolder(pattern)),
                            atlas -> TrimicaClient.getItemModelFactory().clear()
                    ))
            );
            shieldAtlases.put(
                    pattern, new Lazy<>(() -> new RuntimeTrimAtlas(
                            Trimica.rl("%s/%s/shield.png".formatted(patternId.getNamespace(), patternId.getPath())),
                            new TrimShieldSpriteFactory(),
                            (m) -> new ArmorTrim(materials.wrapAsHolder(m), patterns.wrapAsHolder(pattern)),
                            atlas -> {}
                    ))
            );
        }
    }

    private void resetFrames() {
        for(Map<EquipmentClientInfo.LayerType, Lazy<RuntimeTrimAtlas>> layerBasedAtlases : equipmentAtlases.values()) {
            for(Lazy<RuntimeTrimAtlas> atlas : layerBasedAtlases.values()) {
                if (atlas.isPresent()) {
                    atlas.get().resetFrames();
                }
            }
        }
    }

    public RuntimeTrimAtlas getEquipmentAtlas(TrimPattern pattern, EquipmentClientInfo.LayerType layerType) {
        return equipmentAtlases.get(pattern).get(layerType).get();
    }

    public RuntimeTrimAtlas getItemAtlas(TrimPattern pattern) {
        return itemAtlases.get(pattern).get();
    }

    public RuntimeTrimAtlas getShieldAtlas(TrimPattern pattern) {
        return shieldAtlases.get(pattern).get();
    }

    public DynamicTrimTextureAtlasSprite getShieldSprite(DataComponentGetter getter) {
        ArmorTrim trim = getter != null ? getter.get(DataComponents.TRIM) : null;
        if (trim == null) return null;

        TrimModelId trimModelId = TrimModelId.fromTrim("shield", trim, null);
        ResourceLocation overlayLocation = trimModelId.asSingle();
        MaterialAdditions addition = getter.get(MaterialAdditions.TYPE);
        if (addition != null) {
            overlayLocation = addition.apply(overlayLocation);
        }
        return getShieldAtlas(trim.pattern().value()).getSprite(getter, trim.material().value(), overlayLocation);
    }

    public interface TrimFactory {
        ArmorTrim create(TrimMaterial material);
    }
}
