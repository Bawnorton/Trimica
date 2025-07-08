package com.bawnorton.trimica.client.model;

import com.bawnorton.trimica.client.palette.TrimPalette;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.ResourceLocation;

public record TrimmedItemModelWrapper(ItemModel model, TrimPalette palette, ResourceLocation location) {
    public static TrimmedItemModelWrapper noTrim(ItemModel model) {
        return new TrimmedItemModelWrapper(model, null, null);
    }
}
