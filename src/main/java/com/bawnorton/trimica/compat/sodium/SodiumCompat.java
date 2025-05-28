package com.bawnorton.trimica.compat.sodium;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

//? if <=1.21.5 {
import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;

public class SodiumCompat {
    public void markSpriteAsActive(TextureAtlasSprite sprite) {
        SpriteUtil.INSTANCE.markSpriteActive(sprite);
    }
}
//?} else {
/*public class SodiumCompat {
    public void markSpriteAsActive(TextureAtlasSprite sprite) {
    }
}
*///?}
