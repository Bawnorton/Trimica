package com.bawnorton.trimica.mixin.registry.tag;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.data.tags.TrimicaRuntimeTags;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(MappedRegistry.class)
abstract class MappedRegistryMixin<T> implements WritableRegistry<T> {
	@Shadow
	@Final
	private ResourceKey<? extends Registry<T>> key;

	@Shadow
	@Final
	private Map<TagKey<T>, HolderSet.Named<T>> frozenTags;

	@SuppressWarnings("unchecked")
	@Inject(
			method = "freeze",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/core/MappedRegistry;frozen:Z",
					opcode = Opcodes.PUTFIELD
			)
	)
	private void addRuntimeTags(CallbackInfoReturnable<Registry<T>> cir) {
		if(key.equals(Registries.TRIM_MATERIAL)) {
			TrimicaRuntimeTags runtimeTags = Trimica.getRuntimeTags();
			Set<HolderSet.Named<TrimMaterial>> tags = runtimeTags.bindTags((Registry<TrimMaterial>) this);
			tags.forEach(tag -> frozenTags.put((TagKey<T>) tag.key(), (HolderSet.Named<T>) tag));
			runtimeTags.clearUnbound();
		}
	}
}