package com.bawnorton.trimica.client.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net/minecraft/client/renderer/texture/SpriteContents$Ticker")
public interface SpriteContents$TickerAccessor {
    @Accessor("frame")
    void trimica$frame(int frame);
}
