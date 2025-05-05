package com.bawnorton.trimica.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;

public class DynamicTextureAtlasSprite extends TextureAtlasSprite {
    private RenderType renderType;

    private DynamicTextureAtlasSprite(ResourceLocation location, SpriteContents spriteContents, int width, int height) {
        super(location, spriteContents, width, height, 0, 0);
    }

    public static DynamicTextureAtlasSprite create(ResourceLocation location, NativeImage image, int width, int height, boolean decal) {
        SpriteContents dummyContents = new SpriteContents(location, new FrameSize(width, height), image, ResourceMetadata.EMPTY);
        DynamicTextureAtlasSprite sprite = new DynamicTextureAtlasSprite(location, dummyContents, width, height);
        DynamicTexture texture = new DynamicTexture(location::toString, image);
        ResourceLocation textureId = location.withSuffix(".png");
        Minecraft.getInstance().getTextureManager().register(textureId, texture);
        sprite.renderType = decal ? RenderType.createArmorDecalCutoutNoCull(textureId) : RenderType.armorCutoutNoCull(textureId);
        return sprite;
    }

    public RenderType getRenderType() {
        return renderType;
    }
}
