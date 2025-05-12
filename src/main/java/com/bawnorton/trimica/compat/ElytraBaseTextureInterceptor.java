package com.bawnorton.trimica.compat;

import com.bawnorton.trimica.api.BaseTextureInterceptor;
import com.bawnorton.trimica.api.TrimmedType;
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
    public ResourceLocation intercept(@Nullable ResourceLocation expectedBaseTexture, ItemStack itemWithTrim, ArmorTrim armourTrim, TrimmedType trimmedType) {
        if (itemWithTrim != null && itemWithTrim.getItem() != Items.ELYTRA) return expectedBaseTexture;

        return switch (trimmedType) {
            case ITEM -> ResourceLocation.withDefaultNamespace("textures/trims/items/elytra/default.png");
            case ARMOUR -> {
                TrimPattern pattern = armourTrim.pattern().value();
                String assetId = pattern.assetId().getPath();
                yield  ResourceLocation.withDefaultNamespace("textures/trims/models/elytra/%s.png".formatted(assetId));
            }
            default -> expectedBaseTexture;
        };
    }
}
