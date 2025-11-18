package com.bawnorton.trimica.mixin;

import com.bawnorton.trimica.data.tags.ForcedTagEntry;
import com.bawnorton.trimica.extend.TagAppenderExtension;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagAppender.class)
interface TagAppenderMixin {
	@Mixin(targets = "net/minecraft/data/tags/TagAppender$1")
	abstract class TagAppenderImpl1Mixin<E, T> implements TagAppender<E, T>, TagAppenderExtension<E, T> {
		@Shadow(aliases = "val$builder")
		@Final
		TagBuilder field_60483;

		@Override
		public TagAppender<E, T> trimica$forceAddTag(TagKey<T> tag) {
			field_60483.add(new ForcedTagEntry(tag.location()));
			return this;
		}
	}

	@Mixin(targets = "net/minecraft/data/tags/TagAppender$2")
	abstract class TagAppenderImpl2Mixin<E, T> implements TagAppender<E, T>, TagAppenderExtension<E, T> {
		@Shadow(aliases = "val$tagappender")
		@Final
		TagAppender<E, T> field_60484;

		@Override
		@SuppressWarnings("unchecked")
		public TagAppender<E, T> trimica$forceAddTag(TagKey<T> tag) {
			((TagAppenderExtension<E, T>) field_60484).trimica$forceAddTag(tag);
			return this;
		}
	}
}
