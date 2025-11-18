package com.bawnorton.trimica.extend;

import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;

public interface TagAppenderExtension<E, T> {
	@SuppressWarnings({"UnusedReturnValue", "unchecked"})
	default TagAppender<E, T> trimica$forceAddTag(TagKey<T> tag) {
		return (TagAppender<E, T>) this;
	}
}
