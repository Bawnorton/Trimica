package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.api.impl.TrimicaApiImpl;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;

public class TrimArmourSpriteFactory extends AbstractTrimSpriteFactory {
    private final EquipmentClientInfo.LayerType layerType;

    public TrimArmourSpriteFactory(EquipmentClientInfo.LayerType layerType) {
        super(64, 32);
        this.layerType = layerType;
    }

    @Nullable
    protected TrimSpriteMetadata getSpriteMetadata(ArmorTrim trim, DataComponentGetter componentGetter, ResourceLocation texture) {
        if (!(componentGetter instanceof ItemStack stack)) return null;

        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) return null;

        TrimMaterial material = trim.material().value();
        ResourceKey<EquipmentAsset> assetResourceKey = equippable.assetId().orElse(null);

        TrimPalette palette = TrimicaClient.getPalettes().getOrGeneratePalette(material, assetResourceKey, texture, componentGetter);
        ResourceLocation basePatternTexture = extractBaseTexture(texture, trim.pattern().value().assetId());
        basePatternTexture = TrimicaApiImpl.INSTANCE.applyBaseTextureInterceptorsForArmour(basePatternTexture, stack, trim);
        return new TrimSpriteMetadata(palette, basePatternTexture);
    }

    protected NativeImage createImageFromMetadata(TrimSpriteMetadata metadata) {
        Minecraft minecraft = Minecraft.getInstance();
        try {
            TextureContents contents = TextureContents.load(minecraft.getResourceManager(), metadata.baseTexture());
            return createColouredImage(metadata, contents);
        } catch (IOException e) {
            Trimica.LOGGER.warn("Expected to find \"{}\" but the texture does not exist, trim overlay will not be added to model", metadata.baseTexture());
            return empty();
        }
    }

    private ResourceLocation extractBaseTexture(ResourceLocation texture, ResourceLocation assetId) {
        return texture.withPath("textures/%s/%s.png".formatted(layerType.trimAssetPrefix(), assetId.getPath()));
    }
}