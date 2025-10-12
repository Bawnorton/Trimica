package com.bawnorton.trimica.api;

import com.bawnorton.trimica.api.impl.TrimicaApiImpl;

@SuppressWarnings("unused")
public interface TrimicaApi {
	/**
	 * @param priority The priority of the interceptor. Lower numbers are called first.
	 */
	void registerBaseTextureInterceptor(int priority, BaseTextureInterceptor baseTextureInterceptor);

	/**
	 * @param priority The priority of the interceptor. Lower numbers are called first.
	 */
	void registerCraftingRecipeInterceptor(int priority, CraftingRecipeInterceptor craftingRecipeInterceptor);

	/**
	 * @param priority The priority of the interceptor. Lower numbers are called first.
	 */
	void registerPaletteInterceptor(int priority, PaletteInterceptor paletteInterceptor);

	default void registerBaseTextureInterceptor(BaseTextureInterceptor baseTextureInterceptor) {
		registerBaseTextureInterceptor(0, baseTextureInterceptor);
	}

	default void registerCraftingRecipeInterceptor(CraftingRecipeInterceptor craftingRecipeInterceptor) {
		registerCraftingRecipeInterceptor(0, craftingRecipeInterceptor);
	}

	default void registerPaletteInterceptor(PaletteInterceptor paletteInterceptor) {
		registerPaletteInterceptor(0, paletteInterceptor);
	}

	static TrimicaApi getInstance() {
		return TrimicaApiImpl.INSTANCE;
	}
}
