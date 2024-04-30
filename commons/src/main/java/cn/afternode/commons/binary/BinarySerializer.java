package cn.afternode.commons.binary;

import cn.afternode.commons.serialization.DeserializeInstantiationException;
import cn.afternode.commons.serialization.FieldAccessException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class BinarySerializer {
    /**
     * Serialize an object to WrappedByteBuffer
     * <br>
     * Currently, it supports String, int, short and enum
     * <br>
     * Write collections may cause errors in deserialization
     * @param obj Source object
     * @param size ByteBuffer allocation size
     * @return Serialized
     * @throws FieldAccessException Field access error
     */
    public WrappedByteBuffer serialize(Object obj, int size) {
        Class<?> c = obj.getClass();

        WrappedByteBuffer bb = new WrappedByteBuffer(size);

        short version = 0;
        if (c.isAnnotationPresent(BinarySerialize.Exclude.class)) throw new IllegalArgumentException("Type %s was excluded from serialization".formatted(c.getName()));
        if (c.isAnnotationPresent(BinarySerialize.class)) {
            version = c.getAnnotation(BinarySerialize.class).version();
        }
        bb.writeShort(version);

        for (Field f: c.getDeclaredFields()) {
            if (f.isAnnotationPresent(BinarySerialize.Exclude.class)) continue;
            f.trySetAccessible();

            try {
                Class<?> type = f.getType();
                Object it = f.get(obj);
                if (type == String.class) {
                    bb.writeUtf((String) it);
                } else if (type == int.class) {
                    bb.writeInt((int) it);
                } else if (type == short.class) {
                    bb.writeShort((Short) it);
                } else if (Enum.class.isAssignableFrom(type)) {
                    bb.writeEnum((Enum<?>) it);
                } else {
                    throw new RuntimeException("Unsupported type %s".formatted(type.getName()));
                }
            } catch (IllegalAccessException ex) {
                throw new FieldAccessException(f, ex);
            }
        }

        return bb;
    }

    /**
     * Serialize an object to WrappedByteBuffer with default size
     * @param obj Source object
     * @return Serialized
     * @see #serialize(Object, int)
     */
    public WrappedByteBuffer serialize(Object obj) {
        return serialize(obj, WrappedByteBuffer.DEFAULT_SIZE);
    }

    /**
     * Deserialize WrappedByteBuffer to object
     */
    public void deserialize(Object obj, WrappedByteBuffer bb) {
        Class<?> c = obj.getClass();

        short version = 0;
        if (c.isAnnotationPresent(BinarySerialize.Exclude.class)) throw new IllegalArgumentException("Type %s was excluded from serialization".formatted(c.getName()));
        if (c.isAnnotationPresent(BinarySerialize.class)) {
            version = c.getAnnotation(BinarySerialize.class).version();
        }

        short bbVersion = bb.readShort();
        if (bbVersion != version) throw new IllegalArgumentException("Version mismatched, deserializing %s, currently %s".formatted(version, bbVersion));

        for (Field f: c.getDeclaredFields()) {
            if (f.isAnnotationPresent(BinarySerialize.Exclude.class)) continue;
            f.trySetAccessible();

            try {
                Class<?> type = f.getType();

                if (type == String.class) {
                    f.set(obj, bb.readUtf());
                } else if (type == int.class) {
                    f.set(obj, bb.readInt());
                } else if (type == short.class) {
                    f.set(obj, bb.readShort());
                } else if (Enum.class.isAssignableFrom(type)) {
                    Class<? extends Enum<?>> te = (Class<Enum<?>>) type;
                    f.set(obj, bb.tryReadEnum(te));
                } else {
                    throw new RuntimeException("Unsupported type %s".formatted(type.getName()));
                }
            } catch (IllegalAccessException ex) {
                throw new FieldAccessException(f, ex);
            }
        }
    }

    /**
     * Create instant and deserialize WrappedByteBuffer
     * @param type Object type
     * @param bb Source WrappedByteBuffer
     * @param <T> Object type
     * @throws DeserializeInstantiationException Error in creating instance
     */
    public <T> T deserialize(Class<T> type, WrappedByteBuffer bb) {
        try {
            Constructor<T> c = type.getDeclaredConstructor();
            c.trySetAccessible();
            T obj = c.newInstance();
            deserialize(obj, bb);
            return obj;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException th) {
            throw new DeserializeInstantiationException("Type %s".formatted(type.getName()), th);
        }
    }
}
