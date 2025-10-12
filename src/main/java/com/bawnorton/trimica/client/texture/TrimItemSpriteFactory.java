package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.api.impl.TrimicaApiImpl;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.item.component.ComponentUtil;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
		super(16, 16);
	}

	@Override
	protected @Nullable TrimSpriteMetadata getSpriteMetadata(ArmorTrim trim, @Nullable DataComponentGetter componentGetter, ResourceLocation texture) {
		if (!(componentGetter instanceof ItemStack stack)) return null;

		ArmorType armourType = ComponentUtil.getArmourType(componentGetter);
		if (armourType == null) return null;

		Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
		assert equippable != null;

		ResourceKey<EquipmentAsset> assetResourceKey = equippable.assetId().orElse(null);
		TrimMaterial material = trim.material().value();
		TrimPalette palette = TrimicaClient.getPalettes().getOrGeneratePalette(material, assetResourceKey, texture, componentGetter);
		ResourceLocation basePatternTexture;
		if (Trimica.enablePerPatternItemTextures) {
			basePatternTexture = TrimicaApiImpl.INSTANCE.applyBaseTextureInterceptorsForItem(getPatternBasedTrimOverlay(armourType, trim), stack, trim);
		} else {
			basePatternTexture = getDefaultTrimOverlay(armourType);
		}
		if(basePatternTexture == null) {
			Trimica.LOGGER.error("Provided base pattern texture for trim overlay is null: Pattern[{}]", trim.pattern().unwrapKey().orElse(null));
			return null;
		}

		return new TrimSpriteMetadata(trim, palette, basePatternTexture, armourType);
	}

	@Override
	protected NativeImage createImageFromMetadata(TrimSpriteMetadata metadata) {
		try {
			TextureContents contents;
			try {
				contents = TextureContents.load(Minecraft.getInstance().getResourceManager(), metadata.baseTexture());
			} catch (FileNotFoundException e) {
				ArmorType armourType = metadata.armorType();
				ResourceLocation defaultTexture = getDefaultTrimOverlay(armourType);
				contents = TextureContents.load(Minecraft.getInstance().getResourceManager(), defaultTexture);
			}
			return createColouredImage(metadata, contents);
		} catch (IOException | RuntimeException e) {
			Trimica.LOGGER.error("Failed to load texture", e);
		}
		return empty();
	}

	private ResourceLocation getPatternBasedTrimOverlay(ArmorType armourType, ArmorTrim trim) {
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
