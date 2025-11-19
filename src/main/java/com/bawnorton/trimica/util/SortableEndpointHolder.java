package com.bawnorton.trimica.util;

import org.jetbrains.annotations.NotNull;

public record SortableEndpointHolder<T>(T endpoint, int priority) implements Comparable<SortableEndpointHolder<T>> {
		@Override
		public int compareTo(@NotNull SortableEndpointHolder<T> o) {
			return Integer.compare(this.priority, o.priority);
		}
	}