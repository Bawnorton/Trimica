package com.bawnorton.trimica.mixin.component;

import com.bawnorton.trimica.item.component.ComponentUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataComponentHolder.class)
public interface DataComponentHolderMixin extends DataComponentGetter {
	@ModifyReturnValue(
			method = "get",
			at = @At("RETURN")
	)
	default <T> T fakeComponents(T original, DataComponentType<? extends T> type) {
		return ComponentUtil.getFakeComponents(this, original, type);
	}
}
