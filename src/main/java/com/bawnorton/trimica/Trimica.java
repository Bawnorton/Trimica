package com.bawnorton.trimica;

import com.bawnorton.trimica.api.TrimicaApi;
import com.bawnorton.trimica.crafting.DefaultCraftingRecipeInterceptor;
import com.bawnorton.trimica.trim.TrimMaterialRuntimeRegistry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trimica {
    public static final String MOD_ID = "trimica";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final TrimMaterialRuntimeRegistry MATERIAL_REGISTRY = new TrimMaterialRuntimeRegistry();

    public static void initialize() {
        LOGGER.info("Trimica Initialized");
        TrimicaApi.getInstance().registerCraftingRecipeInterceptor(new DefaultCraftingRecipeInterceptor());
    }

    public static TrimMaterialRuntimeRegistry getMaterialRegistry() {
        return MATERIAL_REGISTRY;
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
