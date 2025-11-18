package com.bawnorton.trimica.client.palette;

import com.bawnorton.trimica.TrimicaToggles;
import com.bawnorton.trimica.api.PaletteInterceptor;
import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;

public class DefaultPaletteInterceptor implements PaletteInterceptor {
	@Override
	public TrimPalette interceptMaterialAdditions(TrimPalette palette, MaterialAdditions additions) {
		if(palette == TrimPalette.MISSING || palette == TrimPalette.DISABLED || palette == TrimPalette.DEFAULT) {
			return palette;
		}

		if (TrimicaToggles.enableItems && TrimicaToggles.enableAnimator) {
			if (additions.matches(BuiltInRegistries.ITEM.getKey(TrimicaItems.ANIMATOR))) {
				palette = palette.asAnimated();
			}
		}
		if (additions.matches(BuiltInRegistries.ITEM.getKey(Items.GLOW_INK_SAC))) {
			palette.setEmissive(true);
		}
		return palette;
	}
}
