package com.bawnorton.trimica;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.trimica.api.TrimicaApi;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.crafting.DefaultCraftingRecipeInterceptor;
import com.bawnorton.trimica.trim.TrimMaterialRuntimeRegistry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trimica {
    public static final String MOD_ID = "trimica";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /**
     * Whether the rainbowifier item should be enabled.
     * Disabling this will prevent the rainbowifier trim material from being added to the game
     */
    @Configurable
    public static boolean enableRainbowifier = true;

    /**
     * Whether the animator item should be enabled.
     * Disabling this will prevent the animator item from being added to the game
     * and any functionality related to it will be disabled.
     */
    @Configurable
    public static boolean enableAnimator = true;

    /**
     * Whether the items Trimica adds should be enabled.
     * Disabling this will allow you to join servers that don't have Trimica installed
     */
    @Configurable
    public static boolean enableItems = true;

    /**
     * Whether to enable per-pattern item textures.
     * Disabling this will cause all item textures to use the default trim texture for their armour type regardless of the pattern.
     * or the type of item (This will cause elytra trims to use the chestplate texture).
     */
    @Configurable(onSet = "refreshEverything")
    public static boolean enablePerPatternItemTextures = true;

    private static final TrimMaterialRuntimeRegistry MATERIAL_REGISTRY = new TrimMaterialRuntimeRegistry();

    public static void initialize() {
        LOGGER.info("Trimica Initialized");
        TrimicaApi.getInstance().registerCraftingRecipeInterceptor(new DefaultCraftingRecipeInterceptor());
    }

    public static void refreshEverything(Boolean ignored, boolean fromSync) {
        if(!fromSync) return;

        TrimicaClient.refreshEverything();
    }

    public static TrimMaterialRuntimeRegistry getMaterialRegistry() {
        return MATERIAL_REGISTRY;
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
