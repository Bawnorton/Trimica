package com.bawnorton.trimica.mixin.component;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.item.component.MaterialAddition;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DataComponents.class)
public abstract class DataComponentsMixin {
    static {
        MaterialAddition.TYPE = Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE, Trimica.rl("animated_trim"), DataComponentType.<MaterialAddition>builder()
                                                                                                     .persistent(MaterialAddition.CODEC)
                                                                                                     .networkSynchronized(MaterialAddition.STREAM_CODEC)
                                                                                                     .cacheEncoding()
                                                                                                     .build()
        );
    }
}
