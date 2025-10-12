package com.bawnorton.trimica.mixin.component;

import com.bawnorton.trimica.item.component.ComponentUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net/minecraft/core/component/DataComponentMap$Builder$SimpleMap")
public abstract class DataComponentMap$Builder$SimpleMapMixin implements DataComponentMap {
	@ModifyReturnValue(
			method = "get",
			at = @At("RETURN")
	)
	private <T> T fakeComponents(T original, DataComponentType<? extends T> type) {
		return ComponentUtil.getFakeComponents(this, original, type);
	}
}
