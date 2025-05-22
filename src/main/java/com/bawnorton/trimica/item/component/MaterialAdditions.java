package com.bawnorton.trimica.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record MaterialAdditions(Set<ResourceLocation> additionKeys) {
    public static DataComponentType<MaterialAdditions> TYPE;

    public static final Codec<MaterialAdditions> CODEC = ResourceLocation.CODEC.listOf().xmap(
            list -> new MaterialAdditions(Set.copyOf(list)),
            materialAddition -> materialAddition == null ? List.of() : List.copyOf(materialAddition.additionKeys)
    );

    public static final StreamCodec<ByteBuf, MaterialAdditions> STREAM_CODEC = ByteBufCodecs.<ByteBuf, ResourceLocation>list().apply(ResourceLocation.STREAM_CODEC).map(
            list -> new MaterialAdditions(Set.copyOf(list)),
            materialAddition -> materialAddition == null ? List.of() : List.copyOf(materialAddition.additionKeys)
    );

    public MaterialAdditions(ResourceLocation addition) {
        this(Set.of(addition));
    }

    public MaterialAdditions and(ResourceLocation addition) {
        Set<ResourceLocation> additions = new HashSet<>(this.additionKeys);
        additions.add(addition);
        return new MaterialAdditions(additions);
    }

    public boolean matches(ResourceLocation key) {
        for (ResourceLocation additionKey : additionKeys) {
            if (key.equals(additionKey)) {
                return true;
            }
        }
        return false;
    }

    public ResourceLocation apply(ResourceLocation id) {
        String additionPrefix = "/additions/";
        String path = id.getPath();
        int index = path.indexOf(additionPrefix);
        StringBuilder newPath;
        if (index != -1) {
            newPath = new StringBuilder(path.substring(0, index + additionPrefix.length()));
        } else {
            newPath = new StringBuilder(path + additionPrefix);
        }
        for (ResourceLocation additionKey : additionKeys) {
            newPath.append(additionKey.getNamespace()).append("/").append(additionKey.getPath()).append("/");
        }
        return id.withPath(newPath.toString());
    }
}
