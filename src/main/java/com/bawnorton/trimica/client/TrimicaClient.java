package com.bawnorton.trimica.client;

import com.bawnorton.trimica.api.TrimicaApi;
import com.bawnorton.trimica.client.model.TrimItemModelFactory;
import com.bawnorton.trimica.client.model.TrimModelId;
import com.bawnorton.trimica.client.palette.DefaultPaletteInterceptor;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.client.palette.TrimPalettes;
import com.bawnorton.trimica.client.texture.DynamicTrimTextureAtlasSprite;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlases;
import com.bawnorton.trimica.compat.Compat;
import com.bawnorton.trimica.compat.elytratrims.ElytraBaseTextureInterceptor;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.trim.ArmorTrim;

public class TrimicaClient {
    private static final TrimPalettes trimPalettes = new TrimPalettes();
    private static final RuntimeTrimAtlases runtimeTrimAtlases = new RuntimeTrimAtlases();
    private static final TrimItemModelFactory trimItemModelFactory = new TrimItemModelFactory();

    static {
        TrimicaApi api = TrimicaApi.getInstance();
        Compat.ifElytraTrimsPresent(() -> api.registerBaseTextureInterceptor(new ElytraBaseTextureInterceptor()));
        api.registerPaletteInterceptor(new DefaultPaletteInterceptor());
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
