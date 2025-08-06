package com.bawnorton.trimica.mixin.component;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.item.component.AdditionalTrims;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;

@MixinEnvironment
@Mixin(DataComponents.class)
public abstract class DataComponentsMixin {
    static {
        MaterialAdditions.TYPE = Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE, Trimica.rl("material_additions"), DataComponentType.<MaterialAdditions>builder()
                                                                                                     .persistent(MaterialAdditions.CODEC)
                                                                                                     .networkSynchronized(MaterialAdditions.STREAM_CODEC)
                                                                                                     .cacheEncoding()
                                                                                                     .build()
        );
        AdditionalTrims.TYPE = Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE, Trimica.rl("additional_trims"), DataComponentType.<AdditionalTrims>builder()
                                                                                                     .persistent(AdditionalTrims.CODEC)
                                                                                                     .networkSynchronized(AdditionalTrims.STREAM_CODEC)
                                                                                                     .cacheEncoding()
                                                                                                     .build()
        );
    }
}
