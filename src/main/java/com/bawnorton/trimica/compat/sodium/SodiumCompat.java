package com.bawnorton.trimica.compat.sodium;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;

public class SodiumCompat {
    public void markSpriteAsActive(TextureAtlasSprite sprite) {
        SpriteUtil.INSTANCE.markSpriteActive(sprite);
    }
}
