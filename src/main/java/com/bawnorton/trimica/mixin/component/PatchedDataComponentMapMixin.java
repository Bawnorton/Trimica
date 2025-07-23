package com.bawnorton.trimica.mixin.component;

import com.bawnorton.trimica.item.component.ComponentUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@MixinEnvironment
@Mixin(PatchedDataComponentMap.class)
public abstract class PatchedDataComponentMapMixin implements DataComponentMap {
    @Shadow @Nullable public abstract <T> T set(DataComponentType<T> dataComponentType, @Nullable T object);

    @ModifyReturnValue(
            method = "get",
            at = @At("RETURN")
    )
    private <T> T fakeComponents(T original, DataComponentType<? extends T> type) {
        return ComponentUtil.getFakeComponents(this, original, type);
    }

    @Inject(
            method = "set",
            at = @At("RETURN")
    )
    private <T> void setFakeComponents(DataComponentType<T> type, @Nullable T object, CallbackInfoReturnable<T> cir) {
        ComponentUtil.setFakeComponents(this::set, this, type, object);
    }
}
