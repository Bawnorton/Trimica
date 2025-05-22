package com.bawnorton.trimica.compat;

import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class SodiumCompat {
    public void markSpriteAsActive(TextureAtlasSprite sprite) {
        SpriteUtil.INSTANCE.markSpriteActive(sprite);
    }
}
