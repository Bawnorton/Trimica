package com.bawnorton.trimica.mixin;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.TrimicaToggles;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;
import java.util.SequencedSet;
import java.util.function.Consumer;

@Mixin(TagLoader.class)
public abstract class TagLoaderMixin {
	@Unique
	private static final ThreadLocal<WritableRegistry<TrimMaterial>> trimica$trimRegistry = new ThreadLocal<>();

	@SuppressWarnings("unchecked")
	@WrapOperation(
			method = "loadTagsForRegistry",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/core/WritableRegistry;key()Lnet/minecraft/resources/ResourceKey;"
			)
	)
	private static <T> ResourceKey<T> captureTrimRegistry(WritableRegistry<T> instance, Operation<ResourceKey<T>> original) {
		ResourceKey<T> key = original.call(instance);
		if(key.equals(Registries.TRIM_MATERIAL)) {
			trimica$trimRegistry.set((WritableRegistry<TrimMaterial>) instance);
		} else {
			trimica$trimRegistry.remove();
		}
		return key;
	}

	@WrapOperation(
			method = "tryBuildTag",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
					remap = false
			)
	)
	private <E, T> boolean performRuntimeTagManipulation(List<E> instance, E e, Operation<Boolean> original, @Local SequencedSet<T> set) {
		TagLoader.EntryWithSource entryWithSource = (TagLoader.EntryWithSource) e;
		TagEntry entry = entryWithSource.entry();
		return trimica$ignoreLiteralReferencesToDisabledItems(entry) && original.call(instance, e);
	}

	@SuppressWarnings("unchecked")
	@WrapOperation(
			method = "tryBuildTag",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/tags/TagEntry;build(Lnet/minecraft/tags/TagEntry$Lookup;Ljava/util/function/Consumer;)Z"
			)
	)
	private <T> boolean forwardTrimicaMaterialTagReferences(TagEntry instance, TagEntry.Lookup<T> lookup, Consumer<T> consumer, Operation<Boolean> original) {
		boolean didSucceed = original.call(instance, lookup, consumer);
		if(didSucceed) return true;

		if(trimica$trimRegistry.get() != null) {
			return trimica$tryCreateRuntimeTag(instance, trimica$trimRegistry.get(), (Consumer<Holder.Reference<TrimMaterial>>) consumer);
		}
		return false;
	}

	@Unique
	private static boolean trimica$tryCreateRuntimeTag(TagEntry entry, WritableRegistry<TrimMaterial> registry, Consumer<Holder.Reference<TrimMaterial>> consumer) {
		String entryId = entry.toString();
		int trimicaGeneratedIndex = entryId.indexOf("trimica:generated/");
		if (trimicaGeneratedIndex == -1) return false;

		String materialName = entryId.substring(trimicaGeneratedIndex + "trimica:generated/".length());
		int slashIndex = materialName.indexOf('/');
		if (slashIndex == -1) return false;

		String materialNamespace = materialName.substring(0, slashIndex);
		String materialPath = materialName.substring(slashIndex + 1);
		if(materialPath.endsWith("?")) {
			materialPath = materialPath.substring(0, materialPath.length() - 1);
		}
		ResourceLocation materialLocation = ResourceLocation.tryBuild(materialNamespace, materialPath);
		if (materialLocation == null) return false;

		Optional<Holder.Reference<Item>> item = BuiltInRegistries.ITEM.get(materialLocation);
		if (item.isEmpty()) {
			Trimica.LOGGER.warn("Could not find item \"{}\" to create trim material tag for", materialLocation);
			return false;
		}

		Holder.Reference<TrimMaterial> material = Trimica.getRuntimeTags().ceateRuntimeTagForMaterial(item.orElseThrow(), registry);
		if (material == null) return false;

		consumer.accept(material);
		return true;
	}

	@Unique
	private static boolean trimica$ignoreLiteralReferencesToDisabledItems(TagEntry entry) {
		String entryId = entry.toString();
		if (!TrimicaToggles.enableRainbowifier || !TrimicaToggles.enableItems) {
			if (entryId.equals("trimica:rainbowifier")) return false;
		}
		if (!TrimicaToggles.enableAnimator || !TrimicaToggles.enableItems) {
			if (entryId.equals("trimica:animator")) return false;
		}
		if (!TrimicaToggles.enableItems) {
			return !entryId.equals("trimica:fake_addition");
		}
		return true;
	}
}
