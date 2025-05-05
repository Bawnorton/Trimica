package com.bawnorton.trimica.client;

import com.bawnorton.trimica.client.texture.palette.TrimPaletteGenerator;

public class TrimicaClient {
    private static final TrimPaletteGenerator paletteGenerator = new TrimPaletteGenerator();

    public static TrimPaletteGenerator getPaletteGenerator() {
        return paletteGenerator;
    }
}
