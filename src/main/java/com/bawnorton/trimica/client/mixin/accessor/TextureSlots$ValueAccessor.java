package com.bawnorton.trimica.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@MixinEnvironment(value = "client")
@Mixin(TextureSlots.Value.class)
public interface TextureSlots$ValueAccessor {
	@Invoker("<init>")
	static TextureSlots.Value trimica$init(Material material) {
		throw new AssertionError();
	}
}
