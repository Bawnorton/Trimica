package com.bawnorton.trimica.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.texture.SpriteTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@MixinEnvironment(value = "client")
@Mixin(targets = "net/minecraft/client/renderer/texture/TextureAtlasSprite$1")
public interface TextureAtlasSprite$TickerAccessor {
    //? if fabric {
    @Accessor("val$ticker")
    //?} else {
    /*@Accessor("val$spriteticker")
    *///?}
    SpriteTicker trimica$ticker();
}
