//? if fabric {
package com.bawnorton.trimica.platform.fabric.data.provider;

import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.tags.TrimicaTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import java.util.concurrent.CompletableFuture;

public class TrimicaTagProvider extends FabricTagProvider.ItemTagProvider {
    public TrimicaTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture, null);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(ItemTags.TRIM_MATERIALS)
                .add(TrimicaItems.RAINBOW_MATERIAL);
        getOrCreateTagBuilder(TrimicaTags.MATERIAL_ADDITIONS)
                .add(TrimicaItems.ANIMATOR_MATERIAL);
    }
}
//?}