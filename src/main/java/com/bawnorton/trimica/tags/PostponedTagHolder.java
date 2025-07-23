package com.bawnorton.trimica.tags;

import com.bawnorton.trimica.Trimica;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//? if fabric {
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
//?}

public class PostponedTagHolder {
    private static final Map<Registry.PendingTags<?>, Map<TagKey<?>, List<?>>> postponedContentsCache = new HashMap<>();
    private static final Map<TagKey<?>, List<?>> postponedTagsContentCache = new HashMap<>();
    private static Map<ResourceKey<? extends Registry<?>>, Registry.PendingTags<?>> postponedTagsMap;

    public static void setPostponedTags(List<Registry.PendingTags<?>> tags) {
        postponedTagsMap = new HashMap<>();
        for (Registry.PendingTags<?> tag : tags) {
            postponedTagsMap.put(tag.key(), tag);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<Holder.Reference<T>> getUnloadedTag(TagKey<T> tagKey) {
        if (postponedTagsMap == null) return List.of();

        List<?> content = postponedTagsContentCache.get(tagKey);
        if (content != null) return (List<Holder.Reference<T>>) content;

        ResourceKey<? extends Registry<T>> registry = tagKey.registry();
        Registry.PendingTags<?> pendingTags = postponedTagsMap.get(registry);
        if (pendingTags == null) return List.of();

        content = getContent(tagKey, pendingTags);
        postponedTagsContentCache.put(tagKey, content);
        return (List<Holder.Reference<T>>) content;
    }

    @SuppressWarnings("unchecked")
    private static <T> @NotNull List<?> getContent(TagKey<T> tagKey, Registry.PendingTags<?> pendingTags) {
        Map<TagKey<?>, List<?>> contents = postponedContentsCache.computeIfAbsent(
                pendingTags, key -> {
                    try {
                        Class<?> clazz = pendingTags.getClass();
                        //? if fabric {
                        MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();
                        String name = resolver.mapFieldName(
                                "intermediary",
                                resolver.unmapClassName("intermediary", clazz.getName()),
                                "field_54031",
                                "Ljava/util/Map;"
                        );
                        Field pendingContentsField = clazz.getDeclaredField(name);
                        //?} else {
                        /*Field pendingContentsField = clazz.getDeclaredField("val$map");
                        *///?}
                        pendingContentsField.setAccessible(true);
                        Map<TagKey<?>, List<?>> pendingContent = (Map<TagKey<?>, List<?>>) pendingContentsField.get(pendingTags);
                        if (pendingContent == null) {
                            Trimica.LOGGER.warn("Pending contents field is null in PendingTags for registry: {}", pendingTags.key().location());
                            return Map.of();
                        }
                        return pendingContent;
                    } catch (ReflectiveOperationException e) {
                        Trimica.LOGGER.error("Failed to access pending contents field in PendingTags", e);
                        return Map.of();
                    }
                }
        );
        List<?> content = contents.get(tagKey);
        if (content == null) {
            Trimica.LOGGER.warn("No content found for tag {} in registry {}", tagKey, pendingTags.key().location());
            content = List.of();
        }
        return content;
    }
}
