package com.bawnorton.trimica.item.trim;

import com.bawnorton.trimica.Trimica;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.equipment.trim.TrimPatterns;

public class TrimicaTrimPatterns {
	public static final ResourceKey<TrimPattern> SOVEREIGN = registerKey("sovereign");
	public static final ResourceKey<TrimPattern> SOAR = registerKey("soar");
	public static final ResourceKey<TrimPattern> HOLLOW = registerKey("hollow");
	public static final ResourceKey<TrimPattern> CRYSTALLINE = registerKey("crystalline");

	public static void bootstrap(BootstrapContext<TrimPattern> context) {
		TrimPatterns.register(context, SOVEREIGN);
		TrimPatterns.register(context, SOAR);
		TrimPatterns.register(context, HOLLOW);
		TrimPatterns.register(context, CRYSTALLINE);
	}

	private static ResourceKey<TrimPattern> registerKey(String name) {
		return ResourceKey.create(Registries.TRIM_PATTERN, Trimica.rl(name));
	}
}
