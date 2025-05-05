package com.bawnorton.trimica.client.mixin.accessor;

import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ItemModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(CompositeModel.class)
public interface CompositeModelAccessor {
    @Accessor("models")
    List<ItemModel> trimica$models();
}
