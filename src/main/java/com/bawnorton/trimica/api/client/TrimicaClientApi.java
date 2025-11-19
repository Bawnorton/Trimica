package com.bawnorton.trimica.api.client;

import com.bawnorton.trimica.api.BaseTextureInterceptor;
import com.bawnorton.trimica.api.PaletteInterceptor;
import com.bawnorton.trimica.api.client.impl.TrimicaClientApiImpl;

public interface TrimicaClientApi {
	TrimicaRenderer getRenderer();
	/**
	 * @param priority The priority of the interceptor. Lower numbers are called first.
	 */
	void registerBaseTextureInterceptor(int priority, BaseTextureInterceptor baseTextureInterceptor);

	/**
	 * @param priority The priority of the interceptor. Lower numbers are called first.
	 */
	void registerPaletteInterceptor(int priority, PaletteInterceptor paletteInterceptor);

	default void registerBaseTextureInterceptor(BaseTextureInterceptor baseTextureInterceptor) {
		registerBaseTextureInterceptor(0, baseTextureInterceptor);
	}

	default void registerPaletteInterceptor(PaletteInterceptor paletteInterceptor) {
		registerPaletteInterceptor(0, paletteInterceptor);
	}

	static TrimicaClientApi getInstance() {
		return TrimicaClientApiImpl.INSTANCE;
	}
}
