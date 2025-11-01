package com.bawnorton.trimica.data.tags;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.mixin.accessor.HolderSet$NamedAccessor;
import com.bawnorton.trimica.trim.TrimMaterialRuntimeRegistry;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ProvidesTrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

import java.util.*;
import java.util.function.Function;

public final class TrimicaRuntimeTags {
	private final Set<UnboundTag> unboundTags = new HashSet<>();
	private final Map<String, Holder.Reference<TrimMaterial>> references = new HashMap<>();

	public KeyHolder createMaterialKeyHolderForItem(Holder.Reference<Item> item) {
		ResourceLocation id = item.key().location();
		ResourceLocation generatedId = Trimica.rl("generated/%s/%s".formatted(id.getNamespace(), id.getPath()));
		ResourceKey<TrimMaterial> resourceKey = ResourceKey.create(Registries.TRIM_MATERIAL, generatedId);
		return new KeyHolder(resourceKey, TagKey.create(Registries.TRIM_MATERIAL, generatedId));
	}

	public Holder.Reference<TrimMaterial> createMaterialTagForItem(Holder.Reference<Item> itemRef, WritableRegistry<TrimMaterial> registry) {
		Item item = itemRef.value();
		ItemStack stack = item.getDefaultInstance();
		ProvidesTrimMaterial materialProvider = stack.get(DataComponents.PROVIDES_TRIM_MATERIAL);
		if(materialProvider == null) {
			if (TrimMaterialRuntimeRegistry.enableTrimEverything) {
				Trimica.LOGGER.warn("Item \"{}\" does not provide a trim material, cannot create runtime tag for it", itemRef);
			}
			return null;
		}

		Holder<TrimMaterial> materialHolder = materialProvider.material()
				.contents()
				.map(Function.identity(), key -> {
					Holder.Reference<TrimMaterial> ref = registry.get(key).orElse(null);
					if (ref == null) {
						Trimica.LOGGER.warn("Item \"{}\" tried to provide a trim material which does not exist, cannot create runtime tag for it", itemRef);
						return null;
					}
					return ref;
				});
		if (materialHolder == null) return null;

		TrimMaterial material = materialHolder.unwrap().map(registry::getValue, Function.identity());
		if (material == null) {
			Trimica.LOGGER.warn("Item \"{}\"'s trim material doesn't exist, cannot create runtime tag for it", itemRef);
			return null;
		}

		KeyHolder keyHolder = createMaterialKeyHolderForItem(itemRef);
		RegistrationInfo registrationInfo = new RegistrationInfo(Optional.empty(), Lifecycle.stable());
		ResourceKey<TrimMaterial> resourceKey = keyHolder.resourceKey();
		Holder.Reference<TrimMaterial> reference = registry.getResourceKey(material)
				.flatMap(registry::get)
				.orElseGet(() -> registry.get(resourceKey).orElseGet(() -> registry.register(resourceKey, material, registrationInfo)));
		references.put(material.assets().base().suffix(), reference);
		unboundTags.add(new UnboundTag(keyHolder.tagKey(), reference));
		Trimica.getMaterialRegistry().registerMaterialReference(reference);
		return reference;
	}

	public Set<HolderSet.Named<TrimMaterial>> bindTags(Registry<TrimMaterial> registry) {
		Set<HolderSet.Named<TrimMaterial>> runtimeMaterialTags = new HashSet<>();
		for (UnboundTag unboundTag : unboundTags) {
			HolderSet.Named<TrimMaterial> holderSet = HolderSet$NamedAccessor.trimica$init(registry, unboundTag.key());
			HolderSet$NamedAccessor accessor = (HolderSet$NamedAccessor) holderSet;
			accessor.trimica$bind(List.of(unboundTag.material()));
			runtimeMaterialTags.add(holderSet);
		}
		return runtimeMaterialTags;
	}

	public void clear() {
		clearUnbound();
		references.clear();
	}

	public void clearUnbound() {
		unboundTags.clear();
	}

	public Holder<TrimMaterial> convertHolder(Holder<TrimMaterial> holder) {
		if (!(holder instanceof Holder.Direct<TrimMaterial>(TrimMaterial material))) return holder;

		String suffix = material.assets().base().suffix();
		return Objects.requireNonNullElse(references.get(suffix), holder);
	}

	private record UnboundTag(TagKey<TrimMaterial> key, Holder.Reference<TrimMaterial> material) {
	}

	public record KeyHolder(ResourceKey<TrimMaterial> resourceKey, TagKey<TrimMaterial> tagKey) {
	}
}
