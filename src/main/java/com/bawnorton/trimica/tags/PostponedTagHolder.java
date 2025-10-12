package com.bawnorton.trimica.tags;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.mixin.accessor.MappedRegistry$PendingTagsAnonymousAccessor;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PostponedTagHolder {
	private static final Map<Registry.PendingTags<?>, Map<TagKey<?>, HolderSet<?>>> postponedContentsCache = new HashMap<>();
	private static final Map<TagKey<?>, HolderSet<?>> postponedTagsContentCache = new HashMap<>();
	private static Map<ResourceKey<? extends Registry<?>>, Registry.PendingTags<?>> postponedTagsMap;

	public static void setPostponedTags(List<Registry.PendingTags<?>> tags) {
		postponedTagsMap = new HashMap<>();
		for (Registry.PendingTags<?> tag : tags) {
			postponedTagsMap.put(tag.key(), tag);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> HolderSet<T> getUnloadedTag(TagKey<T> tagKey) {
		if (postponedTagsMap == null) return HolderSet.empty();

		HolderSet<?> content = postponedTagsContentCache.get(tagKey);
		if (content != null) return (HolderSet<T>) content;

		ResourceKey<? extends Registry<T>> registry = tagKey.registry();
		Registry.PendingTags<T> pendingTags = (Registry.PendingTags<T>) postponedTagsMap.get(registry);
		if (pendingTags == null) return HolderSet.empty();

		content = getContent(tagKey, pendingTags);
		postponedTagsContentCache.put(tagKey, content);
		return (HolderSet<T>) content;
	}

	@SuppressWarnings("unchecked")
	private static <T> HolderSet<T> getContent(TagKey<T> tagKey, Registry.PendingTags<T> pendingTags) {
		Map<TagKey<?>, HolderSet<?>> contents = postponedContentsCache.computeIfAbsent(
				pendingTags, key -> {
					if(pendingTags instanceof MappedRegistry$PendingTagsAnonymousAccessor accessor) {
						Map<TagKey<T>, List<Holder<T>>> pendingContent = accessor.trimica$val$pendingContents();
						if (pendingContent == null) {
							Trimica.LOGGER.warn("Pending contents field is null in PendingTags for registry: {}", pendingTags.key().location());
							return Map.of();
						}
						return pendingContent.entrySet()
								.stream()
								.collect(ImmutableMap.toImmutableMap(
										tagKeyListEntry -> tagKeyListEntry != null ? tagKeyListEntry.getKey() : null,
										entry -> entry != null ? HolderSet.direct(entry.getValue()) : HolderSet.empty()
								));
					}
					Trimica.LOGGER.warn("Unknown PendingTags implementation: {} for registry: {}", pendingTags.getClass().getName(), pendingTags.key().location());
					return Map.of();
				}
		);
		HolderSet<T> content = (HolderSet<T>) contents.get(tagKey);
		if (content == null) {
			Trimica.LOGGER.warn("No content found for tag {} in registry {}", tagKey, pendingTags.key().location());
			content = HolderSet.empty();
		}
		return content;
	}
}
