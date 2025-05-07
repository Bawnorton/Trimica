package com.bawnorton.trimica.client.mixin.accessor;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextureAtlas.class)
public interface TextureAtlasAccessor {
    @Accessor("missingSprite")
    TextureAtlasSprite trimica$missingSprite();
}
