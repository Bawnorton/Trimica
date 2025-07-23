package com.bawnorton.trimica.mixin.accessor;

import com.google.common.collect.ImmutableList;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@MixinEnvironment
@Mixin(LootTable.Builder.class)
public interface LootTable$BuilderAccessor {
    @Mutable
    @Accessor("pools")
    void trimica$pools(ImmutableList.Builder<LootPool> pools);

    @Mutable
    @Accessor("functions")
    void trimica$functions(ImmutableList.Builder<LootItemFunction> functions);
}
