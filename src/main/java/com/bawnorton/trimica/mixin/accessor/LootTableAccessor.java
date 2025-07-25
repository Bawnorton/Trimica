package com.bawnorton.trimica.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;
import java.util.Optional;

@MixinEnvironment
@Mixin(LootTable.class)
public interface LootTableAccessor {
    @Accessor("pools")
    List<LootPool> trimica$pools();

    @Accessor("functions")
    List<LootItemFunction> trimica$functions();

    @Accessor("randomSequence")
    Optional<ResourceLocation> trimica$randomSequence();
}
