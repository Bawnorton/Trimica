//? if rei {
/*package com.bawnorton.trimica.mixin.compat.rei;

import com.bawnorton.trimica.compat.rei.TrimicaSmithingDisplayFactory;
import me.shedaniel.rei.plugin.common.DefaultPlugin;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import java.util.List;
import java.util.function.Function;

@Mixin(DefaultPlugin.class)
public abstract class DefaultPluginMixin {
    @ModifyArg(
            method = "registerDisplays",
            at = @At(
                    value = "INVOKE",
                    target = "me/shedaniel/rei/api/common/registry/display/DisplayConsumer$RecipeManagerConsumer$RecipeFillerBuilder.fillMultiple(Ljava/util/function/Function;)V",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args = "classValue=net/minecraft/world/item/crafting/SmithingTrimRecipe",
                            remap = true
                    )
            ),
            remap = false
    )
    private Function<RecipeHolder<SmithingTrimRecipe>, List<DefaultSmithingDisplay>> overrideDefaultTrimmingDisplay(Function<RecipeHolder<SmithingTrimRecipe>, List<DefaultSmithingDisplay>> original) {
        return TrimicaSmithingDisplayFactory::create;
    }
}
*///?}