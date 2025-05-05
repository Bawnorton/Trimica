package com.bawnorton.trimica.client;

import com.bawnorton.trimica.client.texture.palette.TrimPalettes;

public class TrimicaClient {
    private static final TrimPalettes trimPalettes = new TrimPalettes();

    public static TrimPalettes getTrimPalettes() {
        return trimPalettes;
    }
}
