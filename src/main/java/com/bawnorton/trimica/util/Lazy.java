package com.bawnorton.trimica.util;

import java.util.function.Supplier;

public final class Lazy<T> {
    private T value;
    private final Supplier<T> supplier;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }
}
