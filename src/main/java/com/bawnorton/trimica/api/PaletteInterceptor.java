package com.bawnorton.trimica.api;

import com.bawnorton.trimica.client.palette.DefaultPaletteInterceptor;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

/**
 * @see DefaultPaletteInterceptor for the default implementation
 */
@SuppressWarnings("unused")
public interface PaletteInterceptor {
	/**
	 * Intercepts the generated palette for an item.
	 *
	 * @param generated The generated palette
	 * @param material  The material that the generated palette will belong to
	 * @return The palette to use for the trim material. If no changes are needed, return the generated palette.
	 * @deprecated Use {@link #interceptPaletteGeneration(TrimPalette, TrimMaterial, MaterialAdditions)} instead. Removal planned for 2.0.0.
	 */
	@Deprecated(since = "1.4.0")
	default TrimPalette interceptGenerated(TrimPalette generated, TrimMaterial material) {
		return generated;
	}

	/**
	 * Intercepts the palette for a given set of material additions.
	 *
	 * @param palette   The palette to intercept
	 * @param additions The set of material additions on the trimmed item
	 * @return The palette to use for the trimmed item. If no changes are needed, return the original palette.
	 * @apiNote Trimica uses this to convert the palette to an animated one if the "trimica:animator" material addition is present.
	 * @deprecated Use {@link #interceptPaletteGeneration(TrimPalette, TrimMaterial, MaterialAdditions)} instead. Removal planned for 2.0.0.
	 */
	@Deprecated(since = "1.4.0")
	default TrimPalette interceptMaterialAdditions(TrimPalette palette, MaterialAdditions additions) {
		return palette;
	}

	/**
	 * Intercepts the palette during its generation.
	 *
	 * @param palette   The palette to intercept
	 * @param material  The material that the palette will belong to
	 * @param additions The set of material additions on the trimmed item
	 * @return The palette to use for the trimmed item. If no changes are needed, return the original palette.
	 */
	default TrimPalette interceptPaletteGeneration(TrimPalette palette, TrimMaterial material, MaterialAdditions additions) {
		return palette;
	}
}
