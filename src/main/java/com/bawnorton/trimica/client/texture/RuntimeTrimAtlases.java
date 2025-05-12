package com.bawnorton.trimica.client.texture;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class RuntimeTrimAtlases {
    private final Map<TrimPattern, RuntimeTrimAtlasFactory> modelAtlasFactories = new HashMap<>();
    private final Map<TrimPattern, RuntimeTrimAtlasFactory> itemAtlasFactories = new HashMap<>();
    private final Map<TrimPattern, RuntimeTrimAtlasFactory> shieldAtlasFactories = new HashMap<>();

    public void init(RegistryAccess registryAccess) {
        modelAtlasFactories.clear();
        itemAtlasFactories.clear();
        shieldAtlasFactories.clear();

        Registry<TrimPattern> patterns = registryAccess.lookup(Registries.TRIM_PATTERN).orElseThrow();
        Registry<TrimMaterial> materials = registryAccess.lookup(Registries.TRIM_MATERIAL).orElseThrow();
        for (TrimPattern pattern : patterns) {
            modelAtlasFactories.put(
                    pattern, new RuntimeTrimAtlasFactory(
                            material -> new ArmorTrim(materials.wrapAsHolder(material), patterns.wrapAsHolder(pattern)),
                            new TrimArmourSpriteFactory()
                    )
            );
            itemAtlasFactories.put(
                    pattern, new RuntimeTrimAtlasFactory(
                            material -> new ArmorTrim(materials.wrapAsHolder(material), patterns.wrapAsHolder(pattern)),
                            new TrimItemSpriteFactory()
                    )
            );
            shieldAtlasFactories.put(
                    pattern, new RuntimeTrimAtlasFactory(
                            material -> new ArmorTrim(materials.wrapAsHolder(material), patterns.wrapAsHolder(pattern)),
                            new TrimShieldSpriteFactory()
                    )
            );
        }
    }

    public RuntimeTrimAtlas getModelAtlas(ArmorTrim trim) {
        return modelAtlasFactories.get(trim.pattern().value()).getOrCreate(trim.material().value());
    }

    public RuntimeTrimAtlas getItemAtlas(ArmorTrim trim) {
        return itemAtlasFactories.get(trim.pattern().value()).getOrCreate(trim.material().value());
    }

    public RuntimeTrimAtlas getShieldAtlas(ArmorTrim trim) {
        return shieldAtlasFactories.get(trim.pattern().value()).getOrCreate(trim.material().value());
    }

    private static class RuntimeTrimAtlasFactory {
        private final Map<TrimMaterial, RuntimeTrimAtlas> atlases = new HashMap<>();
        private final RuntimeTrimSpriteFactory spriteFactory;
        private final Function<TrimMaterial, ArmorTrim> trimFactory;

        public RuntimeTrimAtlasFactory(Function<TrimMaterial, ArmorTrim> trimFactory, RuntimeTrimSpriteFactory spriteFactory) {
            this.spriteFactory = spriteFactory;
            this.trimFactory = trimFactory;
        }

        public RuntimeTrimAtlas getOrCreate(TrimMaterial material) {
            return atlases.computeIfAbsent(material, this::createAtlas);
        }

        public RuntimeTrimAtlas createAtlas(TrimMaterial material) {
            return new RuntimeTrimAtlas(trimFactory.apply(material), spriteFactory);
        }
    }
}
