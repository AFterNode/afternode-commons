package cn.afternode.commons.mcnbt;

import cn.afternode.commons.serialization.DeserializeInstantiationException;
import cn.afternode.commons.serialization.FieldAccessException;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class NBTSerializer {
    /**
     * Deserialize ReadableNBT to an object
     * <br>
     * If base compound not exists, nothing will be changed
     * <br>
     * Currently, lists are not supported
     *
     * @param nbt Source NBT
     * @param object Destination object, must have SerializableNBT annotation present
     *
     * @throws NullPointerException NBT or object is null
     * @throws IllegalArgumentException Annotation not present
     * @throws RuntimeException Error in deserialization
     *
     * @see de.tr7zw.changeme.nbtapi.NBTType
     */
    public static <T> void deserialize(ReadableNBT nbt, T object) {
        if (nbt == null || object == null) throw new NullPointerException("nbt or object is null");

        Class<T> type = (Class<T>) object.getClass();
        if (!type.isAnnotationPresent(SerializableNBT.class)) throw new IllegalArgumentException("%s has not SerializableNBT annotation".formatted(type.getName()));

        SerializableNBT serializeNBT = type.getAnnotation(SerializableNBT.class);

        ReadableNBT comp = nbt.getCompound(serializeNBT.value());
        if (comp == null) return;

        for (Field f: type.getDeclaredFields()) {
            try {
                if (!f.isAnnotationPresent(SerializableNBT.class)) continue;
                SerializableNBT n = f.getAnnotation(SerializableNBT.class);

                Class<?> fType = f.getType();
                if (!comp.hasTag(n.value())) continue;

                if (List.class.isAssignableFrom(fType)) {
                    throw new IllegalArgumentException("Lists are not supported yet"); // Not supported yet
                } else if (String.class.isAssignableFrom(fType)) {
                    f.set(object, comp.getString(n.value()));   // NBTTagString
                } else if (byte.class.isAssignableFrom(fType)) {
                    f.set(object, comp.getByte(n.value())); // NBTTagByte
                } else if (short.class.isAssignableFrom(fType)) {
                    f.set(object, comp.getShort(n.value()));    // NBTTagShort
                } else if (int.class.isAssignableFrom(fType)) {
                    f.set(object, comp.getInteger(n.value()));  // NBTTagInt
                } else if (long.class.isAssignableFrom(fType)) {
                    f.set(object, comp.getLong(n.value())); // NBTTagLong
                } else if (float.class.isAssignableFrom(fType)) {
                    f.set(object, comp.getFloat(n.value()));    // NBTTagFloat
                } else if (double.class.isAssignableFrom(fType)) {
                    f.set(object, comp.getFloat(n.value()));    // NBTTagDouble
                } else if (byte[].class.isAssignableFrom(fType)) {
                    f.set(object, comp.getByteArray(n.value()));    // NBTTagByteArray
                } else if (int[].class.isAssignableFrom(fType)) {
                    f.set(object, comp.getIntArray(n.value())); // NBTTagIntArray
                } else if (long[].class.isAssignableFrom(fType)) {
                    f.set(object, comp.getLongArray(n.value()));    // NBTTagLongArray
                } else {
                    Object obj = f.get(object);
                    if (obj != null) {
                        deserialize(nbt, obj);
                    } else {
                        f.set(object, deserialize(nbt, fType));
                    }
                }
            } catch (IllegalAccessException t) {
                throw new FieldAccessException(f, t);
            }
        }
    }

    /**
     * Create new object and deserialize NBT
     * @param nbt Source NBT
     * @param type Destination type
     * @return Destination object
     * @throws DeserializeInstantiationException Error in creating instant
     *
     * @see de.tr7zw.changeme.nbtapi.NBTType
     * @see #deserialize(ReadableNBT, Object)
     */
    public static <T> T deserialize(ReadableNBT nbt, Class<T> type) {
        try {
            Constructor<T> c = type.getDeclaredConstructor();
            c.trySetAccessible();
            T obj = c.newInstance();

            if (nbt != null) {
                deserialize(nbt, obj);
            }
            return obj;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new DeserializeInstantiationException("Type %s".formatted(type.getName()), ex);
        }
    }

    /**
     * Serialize object to ReadWriteNBT
     * @param dest Destination NBT
     * @param obj Source object
     *
     * @throws NullPointerException dest or object is null
     * @throws IllegalArgumentException Annotation not present
     * @throws FieldAccessException Field access error
     */
    public static void serialize(ReadWriteNBT dest, Object obj) {
        if (dest == null || obj == null) throw new NullPointerException("dest or obj is null");

        Class<?> objType = obj.getClass();
        if (!objType.isAnnotationPresent(SerializableNBT.class)) throw new IllegalArgumentException("%s was not a serializable object".formatted(objType.getName()));
        SerializableNBT serializableNBT = objType.getAnnotation(SerializableNBT.class);

        ReadWriteNBT comp = dest.getOrCreateCompound(serializableNBT.value());

        for (Field f: objType.getDeclaredFields()) {
            try {
                if (!f.isAnnotationPresent(SerializableNBT.class)) continue;
                SerializableNBT anno = f.getAnnotation(SerializableNBT.class);
                f.trySetAccessible();

                Class<?> type = f.getType();
                String k = anno.value();
                Object o = f.get(anno);
                if (o == null) continue;

                if (List.class.isAssignableFrom(type)) {
                    throw new IllegalArgumentException("Lists are not supported yet");
                } else if (String.class.isAssignableFrom(type)) {
                    comp.setString(k, (String) o);  // NBTTagString
                } else if (byte.class.isAssignableFrom(type)) {
                    comp.setByte(k, (Byte) o);
                } else if (short.class.isAssignableFrom(type)) {
                    comp.setShort(k, (Short) o);
                } else if ( long.class.isAssignableFrom(type)) {
                    comp.setLong(k, (Long) o);
                } else if (float.class.isAssignableFrom(type)) {
                    comp.setFloat(k, (Float) o);
                } else if (double.class.isAssignableFrom(type)) {
                    comp.setDouble(k, (Double) o);
                } else if (byte[].class.isAssignableFrom(type)) {
                    comp.setByteArray(k, (byte[]) o);
                } else if (int[].class.isAssignableFrom(type)) {
                    comp.setByte(k, (Byte) o);
                } else if (long[].class.isAssignableFrom(type)) {
                    comp.setLongArray(k, (long[]) o);
                } else {
                    serialize(comp, o);
                }
            } catch (IllegalAccessException t) {
                throw new FieldAccessException(f, t);
            }
        }
    }
}
