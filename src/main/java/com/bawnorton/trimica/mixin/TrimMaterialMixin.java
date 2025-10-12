package com.bawnorton.trimica.mixin;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
				MaterialAdditions.CODEC.lenientOptionalFieldOf("trimica$additions").forGetter(material -> Optional.ofNullable(Trimica.getMaterialRegistry().getIntrinsicAdditions(material)))
		).apply(
				instance, (trimMaterial, additions) -> {
					Trimica.getMaterialRegistry().setIntrinsicAdditions(trimMaterial, additions.orElse(null));
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
				MaterialAdditions.STREAM_CODEC, (trimMaterial) -> Trimica.getMaterialRegistry().getIntrinsicAdditions(trimMaterial),
				(trimMaterial, additions) -> {
					Trimica.getMaterialRegistry().setIntrinsicAdditions(trimMaterial, additions);
					return trimMaterial;
				}
		);
	}
}
