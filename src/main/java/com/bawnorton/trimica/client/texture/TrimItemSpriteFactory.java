package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.mixin.accessor.TextureAtlasAccessor;
import com.bawnorton.trimica.client.mixin.accessor.TextureAtlasSpriteAccessor;
import com.bawnorton.trimica.client.texture.palette.TrimPalette;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TrimItemSpriteFactory extends AbstractTrimSpriteFactory {
    public TrimItemSpriteFactory() {
        super(16, 16, ((TextureAtlasSpriteAccessor) ((TextureAtlasAccessor) Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS)).trimica$missingSprite()).trimica$atlasSize());
    }

    @Override
    protected NativeImage createImageFromMaterial(TrimMaterial material, @Nullable ItemStack stack, ResourceLocation location) {
        if (stack == null) return empty();

        ArmorType armourType = getArmourType(stack);
        if (armourType == null) return empty();

        ResourceLocation basePatternTexture = getPatternBasedTrimOverlay(armourType, stack);
        if (basePatternTexture == null) return empty();

        try {
            TextureContents contents;
            try {
                contents = TextureContents.load(Minecraft.getInstance().getResourceManager(), basePatternTexture);
            } catch (FileNotFoundException e) {
                ResourceLocation defaultTexture = getDefaultTrimOverlay(armourType);
                contents = TextureContents.load(Minecraft.getInstance().getResourceManager(), defaultTexture);
            }
            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
            assert equippable != null; // verified above

            ResourceKey<EquipmentAsset> assetResourceKey = equippable.assetId().orElse(null);
            TrimPalette palette = TrimicaClient.getPalettes().getOrGeneratePalette(material, assetResourceKey, location);
            NativeImage coloured = createColouredPatternImage(contents.image(), palette.getColours(), palette.isBuiltin());
            contents.close();
            return coloured;
        } catch (IOException e) {
            Trimica.LOGGER.error("Failed to load texture", e);
        }
        return empty();
    }

    private @Nullable ArmorType getArmourType(ItemStack stack) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) return null;

        EquipmentSlot slot = equippable.slot();
        return switch (slot) {
            case FEET -> ArmorType.BOOTS;
            case LEGS -> ArmorType.LEGGINGS;
            case CHEST -> ArmorType.CHESTPLATE;
            case HEAD -> ArmorType.HELMET;
            default -> null;
        };
    }

    private ResourceLocation getPatternBasedTrimOverlay(ArmorType armourType, ItemStack stack) {
        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim == null) return null;

        ResourceKey<TrimPattern> patternKey = trim.pattern().unwrapKey().orElse(null);
        if (patternKey == null) return null;

        ResourceLocation location = patternKey.location();
        return Trimica.rl("textures/trims/items/%s/%s/%s.png".formatted(
                armourType.getName(),
                location.getNamespace(),
                location.getPath()
        ));
    }

    private ResourceLocation getDefaultTrimOverlay(ArmorType armourType) {
        return ResourceLocation.withDefaultNamespace("textures/trims/items/%s_trim.png".formatted(armourType.getName()));
    }
}
