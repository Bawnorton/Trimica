package com.bawnorton.trimica.api;

import com.bawnorton.trimica.api.impl.TrimicaApiImpl;

@SuppressWarnings("unused")
public interface TrimicaApi {
    static TrimicaApi getInstance() {
        return TrimicaApiImpl.INSTANCE;
    }

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
}
