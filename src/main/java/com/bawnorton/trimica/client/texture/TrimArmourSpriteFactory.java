package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.mixin.accessor.TextureAtlasAccessor;
import com.bawnorton.trimica.client.mixin.accessor.TextureAtlasSpriteAccessor;
import com.bawnorton.trimica.client.texture.palette.TrimPalette;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;

public class TrimArmourSpriteFactory extends AbstractTrimSpriteFactory {
    public TrimArmourSpriteFactory() {
        super(64, 32, ((TextureAtlasSpriteAccessor) ((TextureAtlasAccessor) Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET)).trimica$missingSprite()).trimica$atlasSize());
    }

    @Override
    protected NativeImage createImageFromMaterial(TrimMaterial material, @Nullable ItemStack stack, ResourceLocation location) {
        ResourceLocation basePatternTexture = extractBaseTexture(location);
        if (basePatternTexture == null) return empty();

        Minecraft minecraft = Minecraft.getInstance();
        try {
            TextureContents contents = TextureContents.load(minecraft.getResourceManager(), basePatternTexture);
            TrimPalette palette = TrimicaClient.getPalettes().getOrGeneratePalette(material, location);
            NativeImage coloured = createColouredPatternImage(contents.image(), palette);
            contents.close();
            return coloured;
        } catch (IOException e) {
            Trimica.LOGGER.error("Failed to load texture", e);
            return empty();
        }
    }

    private ResourceLocation extractBaseTexture(ResourceLocation location) {
        String path = location.getPath();
        int lastSlash = path.lastIndexOf("_trimica/");
        if (lastSlash != -1) {
            return location.withPath("textures/%s.png".formatted(path.substring(0, lastSlash)));
        }
        return null;
    }
}
