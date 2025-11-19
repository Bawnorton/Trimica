package com.bawnorton.trimica.api;

import com.bawnorton.trimica.api.impl.TrimicaApiImpl;

@SuppressWarnings("unused")
public interface TrimicaApi {
	/**
	 * @param priority The priority of the interceptor. Lower numbers are called first.
	 * @deprecated Use {@link com.bawnorton.trimica.api.client.TrimicaClientApi#registerBaseTextureInterceptor(BaseTextureInterceptor)} instead. Removal planned for 2.0.0.
	 */
	@Deprecated(since = "1.4.0")
	void registerBaseTextureInterceptor(int priority, BaseTextureInterceptor baseTextureInterceptor);

	/**
	 * @param priority The priority of the interceptor. Lower numbers are called first.
	 */
	void registerCraftingRecipeInterceptor(int priority, CraftingRecipeInterceptor craftingRecipeInterceptor);

	/**
	 * @param priority The priority of the interceptor. Lower numbers are called first.
	 * @deprecated Use {@link com.bawnorton.trimica.api.client.TrimicaClientApi#registerPaletteInterceptor(int, PaletteInterceptor)} instead. Removal planned for 2.0.0.
	 */
	@Deprecated(since = "1.4.0")
	void registerPaletteInterceptor(int priority, PaletteInterceptor paletteInterceptor);

	/**
	 * @deprecated Use {@link com.bawnorton.trimica.api.client.TrimicaClientApi#registerBaseTextureInterceptor(BaseTextureInterceptor)} instead. Removal planned for 2.0.0.
	 */
	@Deprecated(since = "1.4.0")
	default void registerBaseTextureInterceptor(BaseTextureInterceptor baseTextureInterceptor) {
		registerBaseTextureInterceptor(0, baseTextureInterceptor);
	}

	default void registerCraftingRecipeInterceptor(CraftingRecipeInterceptor craftingRecipeInterceptor) {
		registerCraftingRecipeInterceptor(0, craftingRecipeInterceptor);
	}

	/**
	 * @deprecated Use {@link com.bawnorton.trimica.api.client.TrimicaClientApi#registerPaletteInterceptor(PaletteInterceptor)} instead. Removal planned for 2.0.0.
	 */
	@Deprecated(since = "1.4.0")
	default void registerPaletteInterceptor(PaletteInterceptor paletteInterceptor) {
		registerPaletteInterceptor(0, paletteInterceptor);
	}

	static TrimicaApi getInstance() {
		return TrimicaApiImpl.INSTANCE;
	}
}
