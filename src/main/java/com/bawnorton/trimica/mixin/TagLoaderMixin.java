package com.bawnorton.trimica.mixin;

import com.bawnorton.trimica.TrimicaToggles;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@MixinEnvironment
@Mixin(TagLoader.class)
public abstract class TagLoaderMixin {
    @WrapOperation(
            method = "tryBuildTag",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                    remap = false
            )
    )
    private <E> boolean dontRecordDisabledItems(List<E> instance, E e, Operation<Boolean> original) {
        TagLoader.EntryWithSource entryWithSource = (TagLoader.EntryWithSource) e;
        String entry = entryWithSource.entry().toString();
        if(!TrimicaToggles.enableRainbowifier || !TrimicaToggles.enableItems) {
            if(entry.equals("trimica:rainbowifier")) {
                return false;
            }
        }
        if(!TrimicaToggles.enableAnimator || !TrimicaToggles.enableItems) {
            if(entry.equals("trimica:animator")) {
                return false;
            }
        }
        if(!TrimicaToggles.enableItems) {
            if(entry.equals("trimica:fake_addition")) {
                return false;
            }
        }
        return original.call(instance, e);
    }
}
