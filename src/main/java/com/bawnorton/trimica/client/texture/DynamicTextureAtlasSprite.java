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
    private final float mimicSize;

    private DynamicTextureAtlasSprite(ResourceLocation location, SpriteContents spriteContents, int width, int height, float mimicSize) {
        super(location, spriteContents, width, height, 0, 0);
        this.mimicSize = mimicSize;
    }

    public static DynamicTextureAtlasSprite create(ResourceLocation location, NativeImage image, int width, int height, boolean decal, float mimicSize) {
        SpriteContents dummyContents = new SpriteContents(location, new FrameSize(width, height), image, ResourceMetadata.EMPTY);
        DynamicTextureAtlasSprite sprite = new DynamicTextureAtlasSprite(location, dummyContents, width, height, mimicSize);
        DynamicTexture texture = new DynamicTexture(location::toString, image);
        Minecraft.getInstance().getTextureManager().register(location, texture);
        sprite.renderType = decal ? RenderType.createArmorDecalCutoutNoCull(location) : RenderType.armorCutoutNoCull(location);
        return sprite;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    @Override
    public float uvShrinkRatio() {
        return 4 / mimicSize;
    }
}
