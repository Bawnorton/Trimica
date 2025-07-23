package com.bawnorton.trimica.mixin.registry;

import com.bawnorton.trimica.item.TrimicaItems;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;

@MixinEnvironment
@Mixin(Items.class)
public abstract class ItemsMixin {
    static {
        TrimicaItems.forEach((key, item) -> Registry.register(BuiltInRegistries.ITEM, key, item));
    }
}
