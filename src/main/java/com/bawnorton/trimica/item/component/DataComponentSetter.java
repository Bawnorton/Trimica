package com.bawnorton.trimica.item.component;

import net.minecraft.core.component.DataComponentType;

public interface DataComponentSetter {
	<T> void set(DataComponentType<T> type, T value);
}
