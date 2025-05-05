package com.bawnorton.trimica;

import com.bawnorton.trimica.trim.material.RuntimeTrimMaterials;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trimica {
    public static final String MOD_ID = "trimica";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void initialize() {
        LOGGER.info("Trimica Initialized");
        BuiltInRegistries.ITEM.forEach(item -> RuntimeTrimMaterials.getOrCreate(item.getDefaultInstance()));
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
