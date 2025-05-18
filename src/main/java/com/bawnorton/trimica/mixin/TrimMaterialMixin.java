package com.bawnorton.trimica.mixin;

import com.bawnorton.trimica.Trimica;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import java.util.Optional;
import java.util.function.Function;

@Mixin(TrimMaterial.class)
public abstract class TrimMaterialMixin {
    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"
            ),
            remap = false
    )
    private static Function<RecordCodecBuilder.Instance<TrimMaterial>, ? extends App<RecordCodecBuilder.Mu<TrimMaterial>, TrimMaterial>> wrapCodec(Function<RecordCodecBuilder.Instance<TrimMaterial>, ? extends App<RecordCodecBuilder.Mu<TrimMaterial>, TrimMaterial>> builder) {
        return instance -> instance.group(
                RecordCodecBuilder.mapCodec(builder).forGetter(Function.identity()),
                Codec.BOOL.optionalFieldOf("trimica$animated").forGetter(material -> Optional.of(Trimica.getMaterialRegistry().getIsAnimated(material)))
        ).apply(
                instance, (trimMaterial, animated) -> {
                    Trimica.getMaterialRegistry().setIsAnimated(trimMaterial, animated.orElse(false));
                    return trimMaterial;
                }
        );
    }

    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Ljava/util/function/BiFunction;)Lnet/minecraft/network/codec/StreamCodec;"
            )
    )
    private static StreamCodec<RegistryFriendlyByteBuf, TrimMaterial> wrapStreamCodec(StreamCodec<RegistryFriendlyByteBuf, TrimMaterial> original) {
        return StreamCodec.composite(
                original, Function.identity(),
                ByteBufCodecs.BOOL, (trimMaterial) -> Trimica.getMaterialRegistry().getIsAnimated(trimMaterial),
                (trimMaterial, animated) -> {
                    Trimica.getMaterialRegistry().setIsAnimated(trimMaterial, animated);
                    return trimMaterial;
                }
        );
    }
}
