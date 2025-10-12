package com.bawnorton.trimica;

import com.bawnorton.configurable.Configurable;

public class TrimicaToggles {
	/**
	 * Whether the rainbowifier item should be enabled.
	 * Disabling this will prevent the rainbowifier trim material from being added to the game
	 */
	@Configurable
	public static boolean enableRainbowifier = true;

	/**
	 * Whether the animator item should be enabled.
	 * Disabling this will prevent the animator item from being added to the game
	 * and any functionality related to it will be disabled.
	 */
	@Configurable
	public static boolean enableAnimator = true;

	/**
	 * Whether the items Trimica adds should be enabled.
	 * Disabling this will allow you to join servers that don't have Trimica installed
	 */
	@Configurable
	public static boolean enableItems = true;
}
