//? if fabric {
package com.bawnorton.trimica.platform.fabric.mixin.accessor;

import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBakedItemModel;
import net.minecraft.client.renderer.item.ItemModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WrapperBakedItemModel.class)
public interface WrapperBakedItemModelAccessor {
    @Accessor("wrapped")
    ItemModel trimica$wrapped();
}
//?}