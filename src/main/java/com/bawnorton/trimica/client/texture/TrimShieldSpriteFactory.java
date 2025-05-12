package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.api.impl.TrimicaApiImpl;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.mixin.accessor.TextureAtlasAccessor;
import com.bawnorton.trimica.client.mixin.accessor.TextureAtlasSpriteAccessor;
import com.bawnorton.trimica.client.texture.palette.TrimPalette;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TrimShieldSpriteFactory extends AbstractTrimSpriteFactory {
    public TrimShieldSpriteFactory() {
        super(64, 64, ((TextureAtlasSpriteAccessor) ((TextureAtlasAccessor) Minecraft.getInstance().getModelManager().getAtlas(Sheets.SHIELD_SHEET)).trimica$missingSprite()).trimica$atlasSize());
    }

    @Override
    protected NativeImage createImageFromMaterial(ArmorTrim trim, DataComponentGetter componentGetter, ResourceLocation location) {
        ResourceLocation basePatternTexture = getPatternBasedTrimOverlay(trim);
        basePatternTexture = TrimicaApiImpl.INSTANCE.applyBaseTextureInterceptorsForShield(basePatternTexture, componentGetter, trim);
        if (basePatternTexture == null) return empty();

        try {
            TextureContents contents;
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            try {
                contents = TextureContents.load(resourceManager, basePatternTexture);
            } catch (FileNotFoundException e) {
                contents = TextureContents.load(resourceManager, getDefaultTrimOverlay());
            }
            TrimMaterial material = trim.material().value();
            TrimPalette palette = TrimicaClient.getPalettes().getOrGeneratePalette(material, null, location);
            NativeImage coloured = createColouredPatternImage(contents.image(), palette.getColours(), palette.isBuiltin());
            contents.close();
            return coloured;
        } catch (IOException e) {
            Trimica.LOGGER.warn("Expected to find \"{}\" but the texture does not exist, trim overlay will not be added to model", basePatternTexture);
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
