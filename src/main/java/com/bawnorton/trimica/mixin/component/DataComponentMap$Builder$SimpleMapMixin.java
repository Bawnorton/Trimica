package com.bawnorton.trimica.mixin.component;

import com.bawnorton.trimica.item.component.ComponentUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net/minecraft/core/component/DataComponentMap$Builder$SimpleMap")
public abstract class DataComponentMap$Builder$SimpleMapMixin implements DataComponentMap {
    @Shadow @Nullable public abstract <T> T get(@NotNull DataComponentType<? extends T> dataComponentType);

    @ModifyReturnValue(
            method = "get",
            at = @At("RETURN")
    )
    private <T> T fakeComponents(T original, DataComponentType<? extends T> type) {
        return ComponentUtil.getFakeComponents(this, original, type);
    }
}
