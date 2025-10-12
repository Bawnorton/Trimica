package com.bawnorton.trimica.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.concurrent.Executor;

@MixinEnvironment(value = "client")
@Mixin(SpriteLoader.class)
public interface SpriteLoaderAccessor {
	@Invoker("stitch")
	SpriteLoader.Preparations trimica$stitch(List<SpriteContents> sprites, int mipLevel, Executor executor);
}
