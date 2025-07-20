package com.bawnorton.trimica.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;
import java.util.Map;

@MixinEnvironment(value = "client")
@Mixin(TextureAtlas.class)
public interface TextureAtlasAccessor {
    @Accessor("missingSprite")
    void trimica$missingSprite(TextureAtlasSprite missingSprite);

    @Accessor("texturesByName")
    Map<ResourceLocation, TextureAtlasSprite> trimica$texturesByName();

    @Accessor("texturesByName")
    void trimica$texturesByName(Map<ResourceLocation, TextureAtlasSprite> texturesByName);

    @Accessor("sprites")
    void trimica$sprites(List<SpriteContents> sprites);

    @Accessor("animatedTextures")
    List<TextureAtlasSprite.Ticker> trimica$animatedTextures();

    @Accessor("animatedTextures")
    void trimica$animatedTextures(List<TextureAtlasSprite.Ticker> animatedTextures);
}
