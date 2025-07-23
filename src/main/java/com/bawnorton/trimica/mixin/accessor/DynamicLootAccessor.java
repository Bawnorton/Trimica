package com.bawnorton.trimica.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@MixinEnvironment
@Mixin(DynamicLoot.class)
public interface DynamicLootAccessor {
    @Accessor("name")
    ResourceLocation trimica$name();
}
