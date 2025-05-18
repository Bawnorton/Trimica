package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.api.impl.TrimicaApiImpl;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TrimShieldSpriteFactory extends AbstractTrimSpriteFactory {
    public TrimShieldSpriteFactory() {
        super(64, 64);
    }

    @Override
    protected @Nullable TrimSpriteMetadata getSpriteMetadata(ArmorTrim trim, DataComponentGetter componentGetter, ResourceLocation texture) {
        TrimMaterial material = trim.material().value();
        TrimPalette palette = TrimicaClient.getPalettes().getOrGeneratePalette(material, null, texture);
        ResourceLocation basePatternTexture = getPatternBasedTrimOverlay(trim);
        basePatternTexture = TrimicaApiImpl.INSTANCE.applyBaseTextureInterceptorsForShield(basePatternTexture, componentGetter, trim);
        return new TrimSpriteMetadata(palette, basePatternTexture);
    }

    @Override
    protected NativeImage createImageFromMetadata(TrimSpriteMetadata metadata) {
        try {
            TextureContents contents;
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            try {
                contents = TextureContents.load(resourceManager, metadata.baseTexture());
            } catch (FileNotFoundException e) {
                contents = TextureContents.load(resourceManager, getDefaultTrimOverlay());
            }
            return createColouredImage(metadata, contents);
        } catch (IOException e) {
            Trimica.LOGGER.warn("Expected to find \"{}\" but the texture does not exist, trim overlay will not be added to model", metadata.baseTexture());
            return empty();
        }
    }

    private ResourceLocation getPatternBasedTrimOverlay(ArmorTrim trim) {
        ResourceKey<TrimPattern> patternKey = trim.pattern().unwrapKey().orElse(null);
        if (patternKey == null) return null;

        ResourceLocation location = patternKey.location();
        return Trimica.rl("textures/trims/items/shield/%s/%s.png".formatted(
                location.getNamespace(),
                location.getPath()
        ));
    }

    private ResourceLocation getDefaultTrimOverlay() {
        return Trimica.rl("textures/trims/items/shield/default.png");
    }
}
