package com.bawnorton.trimica.client;

import com.bawnorton.trimica.api.TrimicaApi;
import com.bawnorton.trimica.client.model.TrimItemModelFactory;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlases;
import com.bawnorton.trimica.client.texture.palette.TrimPalettes;
import com.bawnorton.trimica.compat.ElytraBaseTextureInterceptor;
import com.bawnorton.trimica.platform.Platform;

public class TrimicaClient {
    private static final TrimPalettes trimPalettes = new TrimPalettes();
    private static final RuntimeTrimAtlases runtimeTrimAtlases = new RuntimeTrimAtlases();
    private static final TrimItemModelFactory trimItemModelFactory = new TrimItemModelFactory();

    static {
        TrimicaApi api = TrimicaApi.getInstance();
        if(Platform.isModLoaded("elytratrims")) {
            api.registerBaseTextureInterceptor(0, new ElytraBaseTextureInterceptor());
        }
    }

    public static TrimPalettes getPalettes() {
        return trimPalettes;
    }

    public static RuntimeTrimAtlases getRuntimeAtlases() {
        return runtimeTrimAtlases;
    }

    public static TrimItemModelFactory getItemModelFactory() {
        return trimItemModelFactory;
    }
}
