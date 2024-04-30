package cn.afternode.commons;

import java.util.function.Function;
import java.util.function.Supplier;

public class Lazy<T> {
    private final Supplier<T> supplier;
    private T value = null;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (this.value == null) {
            this.value = supplier.get();
        }
        return this.value;
    }

    public void clear() {
        this.value = null;
    }
}
