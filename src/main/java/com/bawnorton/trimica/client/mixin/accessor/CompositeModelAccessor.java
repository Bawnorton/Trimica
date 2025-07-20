package com.bawnorton.trimica.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ItemModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@MixinEnvironment(value = "client")
@Mixin(CompositeModel.class)
public interface CompositeModelAccessor {
    @Accessor("models")
    List<ItemModel> trimica$models();
}
