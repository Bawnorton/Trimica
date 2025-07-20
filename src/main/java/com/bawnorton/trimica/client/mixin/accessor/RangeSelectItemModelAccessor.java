package com.bawnorton.trimica.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.SequencedCollection;

@MixinEnvironment(value = "client")
@Mixin(RangeSelectItemModel.class)
public interface RangeSelectItemModelAccessor {
    @Accessor("fallback")
    ItemModel trimica$fallback();

    @Accessor("models")
    ItemModel[] trimica$models();
}
