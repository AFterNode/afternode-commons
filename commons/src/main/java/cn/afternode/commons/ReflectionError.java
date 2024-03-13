package cn.afternode.commons;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionError extends RuntimeException {
    private final Class<?> tagetClass;
    private Method targetMethod;
    private Field targetField;
    private Constructor<?> targetConstructor;

    public ReflectionError(Class<?> c, Throwable cause) {
        super(cause);
        this.tagetClass = c;
    }

    public ReflectionError(Method m, Class<?> c, Throwable cause) {
        this(c, cause);
        this.targetMethod = m;
    }

    public ReflectionError(Field f, Class<?> c, Throwable cause) {
        this(c, cause);
        this.targetField = f;
    }

    public <T> ReflectionError(Constructor<?> cons, Class<?> c, Throwable cause) {
        this(c, cause);
        this.targetConstructor = cons;
    }

    public Class<?> getTagetClass() {
        return tagetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Field getTargetField() {
        return targetField;
    }

    public Constructor<?> getTargetConstructor() {
        return targetConstructor;
    }
}
