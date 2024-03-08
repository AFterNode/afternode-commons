package cn.afternode.commons;

public interface Filter<T> {
    boolean accept(T obj);
}
