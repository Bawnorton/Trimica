package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.client.palette.TrimPalette;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.trim.ArmorTrim;

import java.nio.file.Path;

public record TrimSpriteMetadata(ArmorTrim trim, TrimPalette palette, ResourceLocation baseTexture, ArmorType armorType) {
	public TrimSpriteMetadata(ArmorTrim trim, TrimPalette palette, ResourceLocation baseTexture) {
		this(trim, palette, baseTexture, null);
	}

	public boolean isAnimated() {
		return palette.isAnimated();
	}

	public Path toDebugPath() {
		String pattern = trim.pattern().value().assetId().toString().replace(':', '/');
		String material = trim.material().value().assets().base().suffix().replace(':', '/');
		String metadata = palette.getMetadataString();
		String baseTexturePath = baseTexture.getNamespace() + "/" + baseTexture.getPath().replace(':', '/');
		baseTexturePath = baseTexturePath.substring(0, baseTexturePath.lastIndexOf('.'));
		String fileName = String.format("%s/%s/%s/%s", baseTexturePath, pattern, material, metadata);
		return Path.of(fileName);
	}
}
