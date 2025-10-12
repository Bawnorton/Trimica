package com.bawnorton.trimica.mixin.registry;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.tags.TrimicaRuntimeTags;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

	@Shadow
	@Final
	private Map<ResourceKey<T>, Holder.Reference<T>> byKey;

	@Shadow
	@Final
	private Map<T, Holder.Reference<T>> byValue;

	@Shadow
	@Final
	private Map<ResourceLocation, Holder.Reference<T>> byLocation;

	@Shadow
	@Final
	private ObjectList<Holder.Reference<T>> byId;

	@Shadow
	@Final
	private Reference2IntMap<T> toId;

	@Shadow
	@Final
	private Map<ResourceKey<T>, RegistrationInfo> registrationInfos;

	@Shadow
	private Lifecycle registryLifecycle;

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
			Registry<TrimMaterial> self = (Registry<TrimMaterial>) this;
			runtimeTags.bindTags(self);
			Set<HolderSet.Named<TrimMaterial>> tags = runtimeTags.getTags();
			tags.forEach(tag -> frozenTags.put((TagKey<T>) tag.key(), (HolderSet.Named<T>) tag));
			runtimeTags.clear();
		}
	}
}
