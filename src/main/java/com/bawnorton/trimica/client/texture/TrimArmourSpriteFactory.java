package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.api.impl.TrimicaApiImpl;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.texture.palette.TrimPalette;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import java.io.IOException;

public class TrimArmourSpriteFactory extends AbstractTrimSpriteFactory {
    public TrimArmourSpriteFactory() {
        super(64, 32);
    }

    @Override
    protected NativeImage createImageFromMaterial(ArmorTrim trim, DataComponentGetter componentGetter, ResourceLocation texture) {
        if (!(componentGetter instanceof ItemStack stack)) return empty();

        ResourceLocation basePatternTexture = extractBaseTexture(texture);
        basePatternTexture = TrimicaApiImpl.INSTANCE.applyBaseTextureInterceptorsForArmour(basePatternTexture, stack, trim);
        if (basePatternTexture == null) return empty();

        Minecraft minecraft = Minecraft.getInstance();
        try {
            TextureContents contents = TextureContents.load(minecraft.getResourceManager(), basePatternTexture);
            TrimMaterial material = trim.material().value();
            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
            if(equippable == null) return empty();

            ResourceKey<EquipmentAsset> assetResourceKey = equippable.assetId().orElse(null);
            TrimPalette palette = TrimicaClient.getPalettes().getOrGeneratePalette(material, assetResourceKey, texture);
            NativeImage coloured = createColouredPatternImage(contents.image(), palette.getColours(), palette.isBuiltin());
            contents.close();
            return coloured;
        } catch (IOException e) {
            Trimica.LOGGER.warn("Expected to find \"{}\" but the texture does not exist, trim overlay will not be added to model", basePatternTexture);
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
