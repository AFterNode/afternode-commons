package cn.afternode.commons.serialization;

import java.lang.reflect.Field;

/**
 * Trying to serialize/deserialize excluded type/field
 */
public class ExcludedException extends SerializationException {
    private Class<?> type = null;
    private Field field = null;

    public ExcludedException(String message, Class<?> type) {
        super(message);
        this.type = type;
    }

    public ExcludedException(String message, Field field) {
        super(message);
        this.field = field;
    }

    public Class<?> getType() {
        return type;
    }

    public Field getField() {
        return field;
    }
}
