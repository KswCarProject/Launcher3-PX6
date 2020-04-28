package com.android.launcher3.util;

public abstract class Provider<T> {
    public abstract T get();

    public static <T> Provider<T> of(final T value) {
        return new Provider<T>() {
            public T get() {
                return value;
            }
        };
    }
}
