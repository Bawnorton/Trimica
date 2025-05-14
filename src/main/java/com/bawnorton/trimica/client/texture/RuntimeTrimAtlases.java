package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.TrimicaClient;
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

public final class RuntimeTrimAtlases {
    private final Map<TrimPattern, RuntimeTrimAtlas> modelAtlasFactories = new HashMap<>();
    private final Map<TrimPattern, RuntimeTrimAtlas> itemAtlasFactories = new HashMap<>();
    private final Map<TrimPattern, RuntimeTrimAtlas> shieldAtlasFactories = new HashMap<>();

    private final List<Runnable> modelAtlasModifiedListeners = new ArrayList<>();

    public void init(RegistryAccess registryAccess) {
        modelAtlasFactories.clear();
        itemAtlasFactories.clear();
        shieldAtlasFactories.clear();

        Registry<TrimPattern> patterns = registryAccess.lookup(Registries.TRIM_PATTERN).orElseThrow();
        Registry<TrimMaterial> materials = registryAccess.lookup(Registries.TRIM_MATERIAL).orElseThrow();

        for (TrimPattern pattern : patterns) {
            ResourceLocation patternId = pattern.assetId();
            modelAtlasFactories.put(
                    pattern, new RuntimeTrimAtlas(
                            Trimica.rl("%s/%s/model.png".formatted(patternId.getNamespace(), patternId.getPath())),
                            new TrimArmourSpriteFactory(),
                            (m) -> new ArmorTrim(materials.wrapAsHolder(m), patterns.wrapAsHolder(pattern)),
                            () -> {
                                for (Runnable listener : modelAtlasModifiedListeners) {
                                    listener.run();
                                }
                            }
                    )
            );
            itemAtlasFactories.put(
                    pattern, new RuntimeTrimAtlas(
                            Trimica.rl("%s/%s/item.png".formatted(patternId.getNamespace(), patternId.getPath())),
                            new TrimItemSpriteFactory(),
                            (m) -> new ArmorTrim(materials.wrapAsHolder(m), patterns.wrapAsHolder(pattern)),
                            () -> TrimicaClient.getItemModelFactory().clear()
                    )
            );
            shieldAtlasFactories.put(
                    pattern, new RuntimeTrimAtlas(
                            Trimica.rl("%s/%s/shield.png".formatted(patternId.getNamespace(), patternId.getPath())),
                            new TrimShieldSpriteFactory(),
                            (m) -> new ArmorTrim(materials.wrapAsHolder(m), patterns.wrapAsHolder(pattern)),
                            () -> {}
                    )
            );
        }
    }

    public RuntimeTrimAtlas getModelAtlas(TrimPattern pattern) {
        return modelAtlasFactories.get(pattern);
    }

    public RuntimeTrimAtlas getItemAtlas(TrimPattern pattern) {
        return itemAtlasFactories.get(pattern);
    }

    public RuntimeTrimAtlas getShieldAtlas(TrimPattern pattern) {
        return shieldAtlasFactories.get(pattern);
    }

    public void registerModelAtlasModifiedListener(Runnable runnable) {
        modelAtlasModifiedListeners.add(runnable);
    }

    public interface TrimFactory {
        ArmorTrim create(TrimMaterial material);
    }
}
