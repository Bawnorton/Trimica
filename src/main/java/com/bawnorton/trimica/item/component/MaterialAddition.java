package com.bawnorton.trimica.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record MaterialAddition(ResourceLocation key) {
    public static DataComponentType<MaterialAddition> TYPE;

    public static final Codec<MaterialAddition> CODEC = ResourceLocation.CODEC.xmap(
            MaterialAddition::new,
            MaterialAddition::key
    );

    public static final StreamCodec<ByteBuf, MaterialAddition> STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(
            MaterialAddition::new,
            MaterialAddition::key
    );

    public ResourceLocation apply(ResourceLocation id) {
        return id.withSuffix("/addition/%s/%s".formatted(key.getNamespace(), key.getPath()));
    }
}
