package com.bawnorton.trimica.client.mixin.accessor;

import com.mojang.blaze3d.platform.NativeImage;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@MixinEnvironment(value = "client")
@Mixin(SpriteContents.class)
public interface SpriteContentsAccessor {
	@Accessor("originalImage")
	NativeImage trimica$originalImage();
}
