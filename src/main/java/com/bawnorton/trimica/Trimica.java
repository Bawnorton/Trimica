package com.bawnorton.trimica;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.trimica.api.TrimicaApi;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.crafting.DefaultCraftingRecipeInterceptor;
import com.bawnorton.trimica.data.tags.TrimicaRuntimeTags;
import com.bawnorton.trimica.networking.Networking;
import com.bawnorton.trimica.trim.TrimMaterialRuntimeRegistry;
import com.bawnorton.trimica.util.ConditionalLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trimica {
	public static final String MOD_ID = "trimica";
	public static final Logger LOGGER = ConditionalLogger.of(LoggerFactory.getLogger(MOD_ID), () -> TrimicaToggles.debug);

	/**
	 * Whether to enable per-pattern item textures.
	 * Disabling this will cause all item textures to use the default trim texture for their armour type regardless of the pattern.
	 * or the type of item (This will cause elytra trims to use the chestplate texture).
	 */
	@Configurable(onSet = "refreshEverything")
	public static boolean enablePerPatternItemTextures = true;

	private static final TrimMaterialRuntimeRegistry MATERIAL_REGISTRY = new TrimMaterialRuntimeRegistry();
	private static final TrimicaRuntimeTags RUNTIME_TAGS = new TrimicaRuntimeTags();

	public static void initialize() {
		LOGGER.info("Trimica Initialized");
		Networking.init();
		TrimicaApi.getInstance().registerCraftingRecipeInterceptor(new DefaultCraftingRecipeInterceptor());
	}

	public static void refreshEverything(Boolean ignored, boolean fromSync) {
		RUNTIME_TAGS.clear();
		MATERIAL_REGISTRY.clear();

		if (!fromSync) return;

		TrimicaClient.refreshEverything();
	}

	public static TrimMaterialRuntimeRegistry getMaterialRegistry() {
		return MATERIAL_REGISTRY;
	}

	public static TrimicaRuntimeTags getRuntimeTags() {
		return RUNTIME_TAGS;
	}

	public static ResourceLocation rl(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
