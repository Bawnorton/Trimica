package com.bawnorton.trimica.compat.elytratrims;

import com.bawnorton.trimica.api.BaseTextureInterceptor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.Nullable;

/**
 * This interceptor is used to provide custom base textures for the Elytra item when ElytraTrims is present.
 */
public class ElytraBaseTextureInterceptor implements BaseTextureInterceptor {
    @Override
    public ResourceLocation interceptItemTexture(@Nullable ResourceLocation expectedBaseTexture, ItemStack itemWithTrim, ArmorTrim armourTrim) {
        if (itemWithTrim.getItem() != Items.ELYTRA) return expectedBaseTexture;

        return ResourceLocation.withDefaultNamespace("textures/trims/items/elytra/default.png");
    }

    @Override
    public ResourceLocation interceptArmourTexture(@Nullable ResourceLocation expectedBaseTexture, ItemStack itemWithTrim, ArmorTrim armourTrim) {
        if (itemWithTrim.getItem() != Items.ELYTRA) return expectedBaseTexture;

        TrimPattern pattern = armourTrim.pattern().value();
        String assetId = pattern.assetId().getPath();
        return ResourceLocation.withDefaultNamespace("textures/trims/models/elytra/%s.png".formatted(assetId));
    }
}
