package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.client.palette.TrimPalette;
import net.minecraft.client.renderer.texture.SpriteContents;

public record TrimSpriteContents(SpriteContents spriteContents, TrimPalette palette) {
    public static TrimSpriteContents noPalette(SpriteContents spriteContents) {
        return new TrimSpriteContents(spriteContents, null);
    }
}
