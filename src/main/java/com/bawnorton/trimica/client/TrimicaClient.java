package com.bawnorton.trimica.client;

import com.bawnorton.trimica.api.TrimicaApi;
import com.bawnorton.trimica.client.model.TrimItemModelFactory;
import com.bawnorton.trimica.client.networking.ClientNetworking;
import com.bawnorton.trimica.client.palette.DefaultPaletteInterceptor;
import com.bawnorton.trimica.client.palette.TrimPalettes;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlases;
import com.bawnorton.trimica.compat.Compat;
import com.bawnorton.trimica.compat.elytratrims.ElytraBaseTextureInterceptor;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

public class TrimicaClient {
	private static final TrimPalettes trimPalettes = new TrimPalettes();
	private static final RuntimeTrimAtlases runtimeTrimAtlases = new RuntimeTrimAtlases();
	private static final TrimItemModelFactory trimItemModelFactory = new TrimItemModelFactory();

	public static void init() {
		ClientNetworking.init();

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

	public static void refreshEverything() {
		trimPalettes.clear();
		runtimeTrimAtlases.clear();
		trimItemModelFactory.clear();
	}
}
