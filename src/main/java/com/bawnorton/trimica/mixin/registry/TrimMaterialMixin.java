package com.bawnorton.trimica.mixin.registry;

import com.bawnorton.trimica.Trimica;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TrimMaterial.class)
abstract class TrimMaterialMixin {
	@Shadow
	@Final
	@Mutable
	public static Codec<Holder<TrimMaterial>> CODEC;

	static {
		CODEC = CODEC.xmap(Trimica.getRuntimeTags()::convertHolder, Trimica.getRuntimeTags()::convertHolder);
	}

}
