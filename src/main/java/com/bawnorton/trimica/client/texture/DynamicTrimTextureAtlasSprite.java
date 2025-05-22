package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.client.palette.TrimPalette;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynamicTrimTextureAtlasSprite extends TextureAtlasSprite {
    private final TextureAtlasSprite delegate;
    private final RenderType renderType;
    private final TrimPalette palette;

    public DynamicTrimTextureAtlasSprite(TextureAtlasSprite delegate, RenderType renderType, TrimPalette palette) {
        super(delegate.atlasLocation(), delegate.contents(), 1, 1, delegate.getX(), delegate.getY());
        this.delegate = delegate;
        this.renderType = renderType;
        this.palette = palette;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    @Nullable
    public TrimPalette getPalette() {
        return palette;
    }

    @Override
    public int getX() {
        return delegate.getX();
    }

    @Override
    public int getY() {
        return delegate.getY();
    }

    @Override
    public float getU0() {
        return delegate.getU0();
    }

    @Override
    public float getU1() {
        return delegate.getU1();
    }

    @Override
    public @NotNull SpriteContents contents() {
        return delegate.contents();
    }

    @Override
    public @Nullable Ticker createTicker() {
        return delegate.createTicker();
    }

    @Override
    public float getU(float f) {
        return delegate.getU(f);
    }

    @Override
    public float getUOffset(float f) {
        return delegate.getUOffset(f);
    }

    @Override
    public float getV0() {
        return delegate.getV0();
    }

    @Override
    public float getV1() {
        return delegate.getV1();
    }

    @Override
    public float getV(float f) {
        return delegate.getV(f);
    }

    @Override
    public float getVOffset(float f) {
        return delegate.getVOffset(f);
    }

    @Override
    public @NotNull ResourceLocation atlasLocation() {
        return delegate.atlasLocation();
    }

    @Override
    public @NotNull String toString() {
        return "Dynamic{%s, %s}".formatted(delegate, renderType);
    }

    @Override
    public void uploadFirstFrame(@NotNull GpuTexture gpuTexture) {
        delegate.uploadFirstFrame(gpuTexture);
    }

    @Override
    public float uvShrinkRatio() {
        return delegate.uvShrinkRatio();
    }

    @Override
    public @NotNull VertexConsumer wrap(@NotNull VertexConsumer vertexConsumer) {
        return delegate.wrap(vertexConsumer);
    }
}
