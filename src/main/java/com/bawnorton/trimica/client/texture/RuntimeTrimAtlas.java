package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.client.mixin.accessor.SpriteContents$TickerAccessor;
import com.bawnorton.trimica.client.mixin.accessor.TextureAtlasAccessor;
import com.bawnorton.trimica.client.mixin.accessor.TextureAtlasSprite$TickerAccessor;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.compat.Compat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class RuntimeTrimAtlas extends TextureAtlas {
    private final RuntimeTrimSpriteFactory spriteFactory;
    private final RuntimeTrimAtlases.TrimFactory trimFactory;
    private final RenderType renderType;
    private final List<SpriteContents> dynamicSprites = new ArrayList<>();
    private final Map<ResourceLocation, TrimPalette> palettes = new HashMap<>();
    private final Consumer<RuntimeTrimAtlas> onModified;

    public RuntimeTrimAtlas(ResourceLocation atlasLocation, RuntimeTrimSpriteFactory spriteFactory, RuntimeTrimAtlases.TrimFactory trimFactory, Consumer<RuntimeTrimAtlas> onModified) {
        super(atlasLocation);
        this.spriteFactory = spriteFactory;
        this.trimFactory = trimFactory;
        this.renderType = RenderType.armorCutoutNoCull(atlasLocation);
        dynamicSprites.add(createMissing());
        this.onModified = onModified;
    }

    private SpriteContents createMissing() {
        ResourceLocation missingLocation = MissingTextureAtlasSprite.getLocation();
        return spriteFactory.create(missingLocation, null, null).spriteContents();
    }

    @Override
    public @NotNull TextureAtlasSprite getSprite(@NotNull ResourceLocation texture) {
        throw new UnsupportedOperationException("Use getSprite(DataComponentGetter, TrimMaterial, ResourceLocation) instead");
    }

    public @NotNull DynamicTrimTextureAtlasSprite getSprite(DataComponentGetter componentGetter, TrimMaterial material, ResourceLocation texture) {
        Map<ResourceLocation, TextureAtlasSprite> texturesByName = asAccessor().trimica$texturesByName();
        TextureAtlasSprite sprite = texturesByName.get(texture);
        if (sprite == null) {
            TrimTextureAtlasSprite trimTextureAtlasSprite = createSprite(componentGetter, material, texture);
            sprite = trimTextureAtlasSprite.sprite();
            TrimPalette palette = trimTextureAtlasSprite.palette();
            palettes.put(texture, palette);
        }
        return new DynamicTrimTextureAtlasSprite(sprite, renderType, palettes.get(texture));
    }

    private TrimTextureAtlasSprite createSprite(DataComponentGetter componentGetter, TrimMaterial material, ResourceLocation texture) {
        TrimSpriteContents sprite = spriteFactory.create(texture, trimFactory.create(material), componentGetter);
        dynamicSprites.add(sprite.spriteContents());
        SpriteLoader loader = SpriteLoader.create(this);
        SpriteLoader.Preparations preparations = loader.stitch(dynamicSprites, 0, Util.backgroundExecutor());
        AtlasSet.StitchResult result = new AtlasSet.StitchResult(this, preparations);
        result.upload();
        onModified.accept(this);
        Minecraft client = Minecraft.getInstance();
        client.getTextureManager().register(location(), this);
        return new TrimTextureAtlasSprite(asAccessor().trimica$texturesByName().get(texture), sprite.palette());
    }

    public void clearTextureData() {
        asAccessor().trimica$sprites(List.of());
        asAccessor().trimica$animatedTextures(List.of());
        asAccessor().trimica$texturesByName(Map.of());
        asAccessor().trimica$missingSprite(null);
    }

    @SuppressWarnings("resource")
    public void resetFrames() {
        List<TextureAtlasSprite.Ticker> tickers = asAccessor().trimica$animatedTextures();
        for(TextureAtlasSprite.Ticker ticker : tickers) {
            if(ticker instanceof TextureAtlasSprite$TickerAccessor accessor && accessor.trimica$ticker() instanceof SpriteContents$TickerAccessor spriteTicker) {
                spriteTicker.trimica$frame(0);
            }
        }
    }

    private TextureAtlasAccessor asAccessor() {
        return (TextureAtlasAccessor) (Object) this;
    }

    private record TrimTextureAtlasSprite(TextureAtlasSprite sprite, TrimPalette palette) {}
}
