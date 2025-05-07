package com.bawnorton.trimica.client;

import com.bawnorton.trimica.client.texture.RuntimeTrimAtlases;
import com.bawnorton.trimica.client.model.TrimItemModelFactory;
import com.bawnorton.trimica.client.texture.palette.TrimPalettes;

public class TrimicaClient {
    private static final TrimPalettes trimPalettes = new TrimPalettes();
    private static final RuntimeTrimAtlases runtimeTrimAtlases = new RuntimeTrimAtlases();
    private static final TrimItemModelFactory trimItemModelFactory = new TrimItemModelFactory();

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
