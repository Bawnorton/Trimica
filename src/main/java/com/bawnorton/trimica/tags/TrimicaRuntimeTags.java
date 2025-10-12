package com.bawnorton.trimica.tags;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.mixin.accessor.HolderSet$NamedAccessor;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ProvidesTrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

import java.util.*;

public final class TrimicaRuntimeTags {
	private final Set<UnboundTag> unboundTags = new HashSet<>();
	private final Set<HolderSet.Named<TrimMaterial>> runtimeMaterialTags = new HashSet<>();
	private final Map<String, Holder.Reference<TrimMaterial>> references = new HashMap<>();

	public Holder.Reference<TrimMaterial> ceateRuntimeTagForMaterial(Holder.Reference<Item> item, WritableRegistry<TrimMaterial> registry) {
		Item value = item.value();
		ResourceLocation id = item.key().location();
		ProvidesTrimMaterial materialProvider = value.getDefaultInstance().get(DataComponents.PROVIDES_TRIM_MATERIAL);
		if(materialProvider == null) {
			Trimica.LOGGER.warn("Item \"{}\" does not provide a trim material, cannot create runtime tag for it", id);
			return null;
		}

		Holder<TrimMaterial> materialHolder = materialProvider.material().contents().left().orElseThrow();
		TrimMaterial material = materialHolder.value();

		ResourceLocation generatedId = Trimica.rl("generated/%s/%s".formatted(id.getNamespace(), id.getPath()));
		ResourceKey<TrimMaterial> resourceKey = ResourceKey.create(Registries.TRIM_MATERIAL, generatedId);
		TagKey<TrimMaterial> tagKey = TagKey.create(Registries.TRIM_MATERIAL, generatedId);
		RegistrationInfo registrationInfo = new RegistrationInfo(Optional.empty(), Lifecycle.stable());
		Holder.Reference<TrimMaterial> reference = registry.register(resourceKey, material, registrationInfo);
		references.put(material.assets().base().suffix(), reference);
		unboundTags.add(new UnboundTag(tagKey, reference));
		Trimica.getMaterialRegistry().registerMaterialReference(reference);
		return reference;
	}

	public void bindTags(Registry<TrimMaterial> registry) {
		for (UnboundTag unboundTag : unboundTags) {
			HolderSet.Named<TrimMaterial> holderSet = HolderSet$NamedAccessor.trimica$init(registry, unboundTag.key());
			HolderSet$NamedAccessor accessor = (HolderSet$NamedAccessor) holderSet;
			accessor.trimica$bind(List.of(unboundTag.material()));
			runtimeMaterialTags.add(holderSet);
		}
	}

	public Set<HolderSet.Named<TrimMaterial>> getTags() {
		return runtimeMaterialTags;
	}

	public void clear() {
		unboundTags.clear();
		runtimeMaterialTags.clear();
	}

	public Holder<TrimMaterial> convertHolder(Holder<TrimMaterial> holder) {
		if (!(holder instanceof Holder.Direct<TrimMaterial>(TrimMaterial material))) return holder;

		String suffix = material.assets().base().suffix();;
		return Objects.requireNonNullElse(references.get(suffix), holder);
	}

	private record UnboundTag(TagKey<TrimMaterial> key, Holder.Reference<TrimMaterial> material) {
	}
}
