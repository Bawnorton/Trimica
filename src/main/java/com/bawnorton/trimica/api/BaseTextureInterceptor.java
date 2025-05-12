package com.bawnorton.trimica.api;

import com.bawnorton.trimica.compat.ElytraBaseTextureInterceptor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

public interface BaseTextureInterceptor {
    /**
     * Interface for intercepting base texture paths for resolving dynamic trim textures.
     * <br>
     * <br>
     * By default, Trimica will attempt to find where the base texture for a given item with a given pattern can be found.
     * <br>
     * <br>
     * For example a chestplate with a silence pattern will look for a base texture at
     * <pre>
     *     {@code
     *     if trimmedType is ITEM:
     *     "trimica:textures/trims/items/chestplate/silence.png"}
     *     {@code
     *     if trimmedType is ARMOUR:
     *     "minecraft:textures/trims/entity/humanoid/silence.png"}
     * </pre>
     * <br>
     * <br>
     * This is useful for providing support to non-standard item types such as elytras which will look for textures at
     * <pre>
     *     {@code
     *     if trimmedType is ITEM:
     *     "trimica:textures/trims/items/chestplate/pattern.png"}
     *     {@code
     *     if trimmedType is ARMOUR:
     *     "minecraft:textures/trims/entity/wings/pattern.png"}
     * </pre>
     * This is clearly not the correct location for elytra trim base textures as the elytra does not have a chestplate model and that entity texture doesn't exist.
     * <br>
     * <br>
     *
     * @param expectedBaseTexture The expected path for the base greyscale trim texture, may be null if one could not be determined.
     * @param itemWithTrim        The item stack with trim applied, may be null.
     * @param armourTrim          The trim applied to the item stack.
     * @param trimmedType         The type of texture about to be generated.
     * @return The path to the base texture to use, if no changes are needed, return the provided expectedBaseTexture.
     * @see ElytraBaseTextureInterceptor ElytraBaseTextureInterceptor for an example implementation.
     */
    ResourceLocation intercept(@Nullable ResourceLocation expectedBaseTexture, @Nullable ItemStack itemWithTrim, ArmorTrim armourTrim, TrimmedType trimmedType);
}
