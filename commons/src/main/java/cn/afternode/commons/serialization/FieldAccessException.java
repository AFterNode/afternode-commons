package cn.afternode.commons.serialization;

import java.lang.reflect.Field;

/**
 * Field access error in serialization/deserialization
 */
public class FieldAccessException extends SerializationException {
    private final IllegalAccessException illegalAccess;
    private final Field field;

    public FieldAccessException(Field f, IllegalAccessException ex) {
        super("Bad field access: %s".formatted(f), ex);

        this.illegalAccess = ex;
        this.field = f;
    }

    public IllegalAccessException getIllegalAccess() {
        return illegalAccess;
    }

    public Field getField() {
        return field;
    }
}
