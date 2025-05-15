package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.client.mixin.accessor.TextureAtlasAccessor;
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
import java.util.List;
import java.util.Map;

public final class RuntimeTrimAtlas extends TextureAtlas {
    private final RuntimeTrimSpriteFactory spriteFactory;
    private final RuntimeTrimAtlases.TrimFactory trimFactory;
    private final RenderType renderType;
    private final List<SpriteContents> dynamicSprites = new ArrayList<>();
    private final Runnable onModified;

    public RuntimeTrimAtlas(ResourceLocation atlasLocation, RuntimeTrimSpriteFactory spriteFactory, RuntimeTrimAtlases.TrimFactory trimFactory, Runnable onModified) {
        super(atlasLocation);
        this.spriteFactory = spriteFactory;
        this.trimFactory = trimFactory;
        this.renderType = RenderType.armorCutoutNoCull(atlasLocation);
        dynamicSprites.add(createMissing());
        this.onModified = onModified;
    }

    private SpriteContents createMissing() {
        ResourceLocation missingLocation = MissingTextureAtlasSprite.getLocation();
        return spriteFactory.create(missingLocation, null, null);
    }

    @Override
    public @NotNull TextureAtlasSprite getSprite(@NotNull ResourceLocation texture) {
        throw new UnsupportedOperationException("Use getSprite(DataComponentGetter, TrimMaterial, ResourceLocation) instead");
    }

    public @NotNull DynamicTextureAtlasSprite getSprite(DataComponentGetter componentGetter, TrimMaterial material, ResourceLocation texture) {
        Map<ResourceLocation, TextureAtlasSprite> texturesByName = asAccessor().trimica$texturesByName();
        TextureAtlasSprite sprite = texturesByName.get(texture);
        if (sprite == null) {
            sprite = createSprite(componentGetter, material, texture);
        }
        return new DynamicTextureAtlasSprite(sprite, renderType);
    }

    private TextureAtlasSprite createSprite(DataComponentGetter componentGetter, TrimMaterial material, ResourceLocation texture) {
        SpriteContents sprite = spriteFactory.create(texture, trimFactory.create(material), componentGetter);
        dynamicSprites.add(sprite);
        SpriteLoader loader = SpriteLoader.create(this);
        SpriteLoader.Preparations preparations = loader.stitch(dynamicSprites, 0, Util.backgroundExecutor());
        AtlasSet.StitchResult result = new AtlasSet.StitchResult(this, preparations);
        result.upload();
        onModified.run();
        Minecraft client = Minecraft.getInstance();
        client.getTextureManager().register(location(), this);
        return asAccessor().trimica$texturesByName().get(texture);
    }

    public void clearTextureData() {
        asAccessor().trimica$sprites(List.of());
        asAccessor().trimica$animatedTextures(List.of());
        asAccessor().trimica$texturesByName(Map.of());
        asAccessor().trimica$missingSprite(null);
    }

    private TextureAtlasAccessor asAccessor() {
        return (TextureAtlasAccessor) (Object) this;
    }
}
