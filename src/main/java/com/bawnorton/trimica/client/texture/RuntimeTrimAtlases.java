package com.bawnorton.trimica.client.texture;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import java.util.HashMap;
import java.util.Map;

public final class RuntimeTrimAtlases {
    private static final Map<TrimPattern, RuntimeTrimAtlasFactory> MODEL_ATLAS_FACTORIES = new HashMap<>();
    private static final Map<TrimPattern, RuntimeTrimAtlasFactory> ITEM_ATLAS_FACTORIES = new HashMap<>();

    public static void init(RegistryAccess registryAccess) {
        MODEL_ATLAS_FACTORIES.clear();
        ITEM_ATLAS_FACTORIES.clear();

        Registry<TrimPattern> patterns = registryAccess.lookup(Registries.TRIM_PATTERN).orElseThrow();
        Registry<TrimMaterial> materials = registryAccess.lookup(Registries.TRIM_MATERIAL).orElseThrow();
        for (TrimPattern pattern : patterns) {
            MODEL_ATLAS_FACTORIES.put(
                    pattern, material -> new RuntimeTrimAtlas(
                            new ArmorTrim(materials.wrapAsHolder(material), patterns.wrapAsHolder(pattern)),
                            new TrimModelFactory()
                    )
            );
            ITEM_ATLAS_FACTORIES.put(
                    pattern, material -> new RuntimeTrimAtlas(
                            new ArmorTrim(materials.wrapAsHolder(material), patterns.wrapAsHolder(pattern)),
                            new TrimItemFactory()
                    )
            );
        }
    }

    public static RuntimeTrimAtlas getModelAtlas(ArmorTrim trim) {
        return MODEL_ATLAS_FACTORIES.get(trim.pattern().value()).getOrCreate(trim.material().value());
    }

    public static RuntimeTrimAtlas getItemAtlas(ArmorTrim trim) {
        return ITEM_ATLAS_FACTORIES.get(trim.pattern().value()).getOrCreate(trim.material().value());
    }

    private interface RuntimeTrimAtlasFactory {
        Map<TrimMaterial, RuntimeTrimAtlas> atlases = new HashMap<>();

        default RuntimeTrimAtlas getOrCreate(TrimMaterial material) {
            return atlases.computeIfAbsent(material, this::createAtlas);
        }

        RuntimeTrimAtlas createAtlas(TrimMaterial material);
    }
}
