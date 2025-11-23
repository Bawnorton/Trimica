package com.bawnorton.trimica.compat;

import com.bawnorton.trimica.compat.sodium.SodiumCompat;
import com.bawnorton.trimica.platform.Platform;

import java.util.function.Consumer;

public class Compat {
	public static void ifSodiumPresent(Consumer<SodiumCompat> consumer) {
		if (Platform.isModLoaded("sodium")) {
			consumer.accept(new SodiumCompat());
		}
	}

	public static void ifSodiumPresentElse(Runnable ifPresent, Runnable ifAbsent) {
		if (Platform.isModLoaded("sodium")) {
			ifPresent.run();
		} else {
			ifAbsent.run();
		}
	}

	public static void ifElytraTrimsPresent(Runnable runnable) {
		if (Platform.isModLoaded("elytratrims")) {
			runnable.run();
		}
	}
}
