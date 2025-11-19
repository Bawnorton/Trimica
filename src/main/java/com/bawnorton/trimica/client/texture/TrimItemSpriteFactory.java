package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.api.client.impl.TrimicaClientApiImpl;
import com.bawnorton.trimica.api.impl.TrimicaApiImpl;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.trim.TrimmedType;
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
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

public class TrimItemSpriteFactory extends AbstractTrimSpriteFactory {
	public TrimItemSpriteFactory() {
		super(16, 16);
	}

	@Override
	protected @Nullable TrimSpriteMetadata getSpriteMetadata(ArmorTrim trim, @Nullable DataComponentGetter componentGetter, ResourceLocation texture) {
		if (!(componentGetter instanceof ItemStack stack)) return null;

		TrimmedType trimmedType = TrimmedType.of(stack);
		ResourceKey<EquipmentAsset> assetResourceKey = Optional.ofNullable(stack.get(DataComponents.EQUIPPABLE))
				.flatMap(Equippable::assetId)
				.orElse(null);
		TrimMaterial material = trim.material().value();
		TrimPalette palette = TrimicaClient.getPalettes()
				.getOrGeneratePalette(material, assetResourceKey, componentGetter);
		ResourceLocation basePatternTexture;
		if (Trimica.enablePerPatternItemTextures) {
			// TrimicaApiImpl method deprecated for removal in 2.0.0
			basePatternTexture = TrimicaApiImpl.INSTANCE.applyBaseTextureInterceptorsForItem(getPatternBasedTrimOverlay(trimmedType, trim), stack, trim);
			basePatternTexture = TrimicaClientApiImpl.INSTANCE.applyBaseTextureInterceptorsForItem(basePatternTexture, stack, trim);
		} else {
			basePatternTexture = getDefaultTrimOverlay(trimmedType);
		}
		if(basePatternTexture == null) {
			Trimica.LOGGER.error("Provided base pattern texture for trim overlay is null: Pattern[{}]", trim.pattern().unwrapKey().orElse(null));
			return null;
		}

		return new TrimSpriteMetadata(trim, palette, basePatternTexture, trimmedType);
	}

	@Override
	protected NativeImage createImageFromMetadata(TrimSpriteMetadata metadata) {
		TextureContents contents = textureCache.computeIfAbsent(metadata.baseTexture(), k -> {
			try {
				return TextureContents.load(Minecraft.getInstance().getResourceManager(), k);
			} catch (IOException e) {
				TrimmedType trimmedType = metadata.trimmedType();
				ResourceLocation defaultTexture = getDefaultTrimOverlay(trimmedType);
				try {
					return TextureContents.load(Minecraft.getInstance().getResourceManager(), defaultTexture);
				} catch (IOException ex) {
					ex.addSuppressed(e);
					Trimica.LOGGER.warn("Expected to find \"{}\" but the texture does of exist, trim overlay will not be added to model", metadata.baseTexture());
					return new TextureContents(empty(), null);
				}
			}
		});
		return createColouredImage(metadata, contents);
	}

	private ResourceLocation getPatternBasedTrimOverlay(TrimmedType trimmedType, ArmorTrim trim) {
		ResourceLocation location = trim.pattern()
				.unwrap()
				.map(ResourceKey::location, TrimPattern::assetId);
		return Trimica.rl("textures/trims/items/%s/%s/%s.png".formatted(
				trimmedType.getName(),
				location.getNamespace(),
				location.getPath()
		));
	}

	private ResourceLocation getDefaultTrimOverlay(TrimmedType trimmedType) {
		if(trimmedType.isOfArmour()) {
			return ResourceLocation.withDefaultNamespace("textures/trims/items/%s_trim.png".formatted(trimmedType.getName()));
		} else {
			return Trimica.rl("textures/trims/items/%s/default.png".formatted(trimmedType.getName()));
		}
	}
}