package cn.afternode.commons.serialization;

/**
 * Unable to create instant in deserialize
 */
public class DeserializeInstantiationException extends SerializationException {
    public DeserializeInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
