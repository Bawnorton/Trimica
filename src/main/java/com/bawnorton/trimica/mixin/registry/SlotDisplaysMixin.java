package com.bawnorton.trimica.mixin.registry;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.crafting.MaterialAdditionRecipe;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplays;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlotDisplays.class)
public abstract class SlotDisplaysMixin {
    @Inject(
            method = "bootstrap",
            at = @At("TAIL")
    )
    private static void registerAdditional(Registry<SlotDisplay.Type<?>> registry, CallbackInfoReturnable<SlotDisplay.Type<?>> cir) {
        Registry.register(registry, Trimica.rl("material_addition"), MaterialAdditionRecipe.DemoSlotDisplay.TYPE);
    }
}
