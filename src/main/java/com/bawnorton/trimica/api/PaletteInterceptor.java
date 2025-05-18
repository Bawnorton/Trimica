package com.bawnorton.trimica.api;

import com.bawnorton.trimica.client.palette.TrimPalette;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

@SuppressWarnings("unused")
public interface PaletteInterceptor {
    /**
     * Intercepts the generated palette for an item.
     *
     * @param generated The generated palette
     * @param material  The material that the generated palette will belong to
     * @return The palette to use for the trim material. If no changes are needed, return the generated palette.
     */
    default TrimPalette intercept(TrimPalette generated, TrimMaterial material) {
        return generated;
    }
}
