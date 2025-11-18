package com.bawnorton.trimica.platform;

//? if fabric {
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Path;

public final class Platform {
    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static boolean isDev() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static Path getDebugDirectory() {
        return FabricLoader.getInstance().getConfigDir().getParent().resolve(".trimica_debug");
    }
}
//?} else if neoforge {
/*
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.FMLLoader;

import java.nio.file.Path;

public final class Platform {
	public static boolean isModLoaded(String modId) {
		ModList modList = ModList.get();
		if (modList != null) {
			return modList.isLoaded(modId);
		}
		//? if >=1.21.10 {
		LoadingModList loadingModList = FMLLoader.getCurrent().getLoadingModList();
		//?} else {
		/^LoadingModList loadingModList = LoadingModList.get();
		^///?}
		if (loadingModList != null) {
			return loadingModList.getModFileById(modId) != null;
		}
		return false;
	}

	//? if >=1.21.10 {
	public static boolean isDev() {
		return !FMLLoader.getCurrent().isProduction();
	}

	public static Path getDebugDirectory() {
		return FMLLoader.getCurrent().getGameDir().resolve(".trimica_debug");
	}
	//?} else {
	/^public static boolean isDev() {
		return !FMLLoader.isProduction();
	}

	public static Path getDebugDirectory() {
		return FMLLoader.getGamePath().resolve(".trimica_debug");
	}
	^///?}
}
*///?}
