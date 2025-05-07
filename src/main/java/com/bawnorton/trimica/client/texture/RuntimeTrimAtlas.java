package com.bawnorton.trimica.client.texture;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public final class RuntimeTrimAtlas {
    private final RuntimeTrimSpriteFactory spriteFactory;
    private final ArmorTrim trim;
    private final Map<ResourceLocation, DynamicTextureAtlasSprite> sprites;

    public RuntimeTrimAtlas(ArmorTrim trim, RuntimeTrimSpriteFactory spriteFactory) {
        this.spriteFactory = spriteFactory;
        this.trim = trim;
        this.sprites = new HashMap<>();
    }

    public @NotNull DynamicTextureAtlasSprite getSprite(ResourceLocation location) {
        return sprites.computeIfAbsent(location, key -> spriteFactory.apply(trim, null, key));
    }

    public @NotNull DynamicTextureAtlasSprite getSprite(ItemStack stack, ResourceLocation location) {
        return sprites.computeIfAbsent(location, key -> spriteFactory.apply(trim, stack, key));
    }
}
