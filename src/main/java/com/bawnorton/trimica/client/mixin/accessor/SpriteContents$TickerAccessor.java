package com.bawnorton.trimica.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@MixinEnvironment(value = "client")
@Mixin(targets = "net/minecraft/client/renderer/texture/SpriteContents$Ticker")
public interface SpriteContents$TickerAccessor {
	@Accessor("frame")
	void trimica$frame(int frame);
}
