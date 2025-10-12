package com.bawnorton.trimica.api;

import com.bawnorton.trimica.compat.elytratrims.ElytraBaseTextureInterceptor;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

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
 *     for the item:
 *     "trimica:textures/trims/items/chestplate/silence.png"}
 *     {@code
 *     for the armour layer:
 *     "minecraft:textures/trims/entity/humanoid/silence.png"}
 * </pre>
 * <br>
 * <br>
 * This is useful for providing support to non-standard item types such as elytras which will look for textures at
 * <pre>
 *     {@code
 *     for the item:
 *     "trimica:textures/trims/items/chestplate/pattern.png"}
 *     {@code
 *     for the armour layer:
 *     "minecraft:textures/trims/entity/wings/pattern.png"}
 * </pre>
 * This is clearly not the correct location for elytra trim base textures as the elytra does not have a chestplate model and that entity texture doesn't exist.
 * <br>
 * <br>
 *
 * @see ElytraBaseTextureInterceptor ElytraBaseTextureInterceptor for an example implementation.
 */
@SuppressWarnings("unused")
public interface BaseTextureInterceptor {
	/**
	 * @param expectedBaseTexture The expected path for the base greyscale trim texture, may be null if one could not be determined.
	 * @param itemWithTrim        The item stack with trim applied.
	 * @param armourTrim          The trim applied to the item stack.
	 * @return The path to the base texture to use, if no changes are needed, return the provided expectedBaseTexture.
	 */
	default ResourceLocation interceptItemTexture(@Nullable ResourceLocation expectedBaseTexture, ItemStack itemWithTrim, ArmorTrim armourTrim) {
		return expectedBaseTexture;
	}

	/**
	 * @param expectedBaseTexture The expected path for the base greyscale trim texture, may be null if one could not be determined.
	 * @param itemWithTrim        The item stack with trim applied.
	 * @param armourTrim          The trim applied to the item stack.
	 * @return The path to the base texture to use, if no changes are needed, return the provided expectedBaseTexture.
	 */
	default ResourceLocation interceptArmourTexture(@Nullable ResourceLocation expectedBaseTexture, ItemStack itemWithTrim, ArmorTrim armourTrim) {
		return expectedBaseTexture;
	}

	/**
	 * @param expectedBaseTexture   The expected path for the base greyscale trim texture, may be null if one could not be determined.
	 * @param shieldComponentGetter The components present on the shield that is about to be rendered.
	 * @param armourTrim            The trim applied to the item stack.
	 * @return The path to the base texture to use, if no changes are needed, return the provided expectedBaseTexture.
	 */
	default ResourceLocation interceptShieldTexture(@Nullable ResourceLocation expectedBaseTexture, DataComponentGetter shieldComponentGetter, ArmorTrim armourTrim) {
		return expectedBaseTexture;
	}
}
