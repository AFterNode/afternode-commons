package cn.afternode.commons.bukkit.configurations;

import cn.afternode.commons.serialization.DeserializeInstantiationException;
import cn.afternode.commons.serialization.FieldAccessException;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ConfigurationsSerializer {
    /**
     * Deserialize configurations into provided object
     * @param config Source configuration
     * @param object Target object
     * @return Deserialized object
     *
     * @throws FieldAccessException Field access error
     */
    public static <T> T deserialize(ConfigurationSection config, T object) {
        for (Field f: object.getClass().getFields()) {
            try {
                if (f.isAnnotationPresent(ConfigSerialization.Ignore.class)) continue;

                String key;
                if (f.isAnnotationPresent(ConfigSerialization.Name.class)) {
                    key = f.getAnnotation(ConfigSerialization.Name.class).value();
                } else key = f.getName();

                if (f.isAnnotationPresent(ConfigSerialization.Serialize.class)) {
                    Object sub = f.get(object);
                    ConfigurationSection sec = config.getConfigurationSection(key);

                    if (sub == null) {
                        sub = deserialize(sec, f.getType());
                        f.set(object, sub);
                    } else {
                        deserialize(sec, sub);
                    }
                } else {
                    f.set(object, config.get(key));
                }
            } catch (IllegalAccessException ex) {
                throw new FieldAccessException(f, ex);
            }
        }

        return object;
    }

    /**
     * Create new instance and deserialize configuration into this object
     * <br>
     * Created instance will be not modified if config is null
     * @param config Source configuration
     * @param type Type
     * @return Created instance
     *
     * @throws DeserializeInstantiationException Error in creating instance
     */
    public static <T> T deserialize(ConfigurationSection config, Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);

            T instance = constructor.newInstance();

            if (config == null) return instance;
            return deserialize(config, instance);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException ex) {
            throw new DeserializeInstantiationException("Type %s".formatted(type.getName()), ex);
        }
    }

    /**
     * Serialize object into ConfigurationSection
     * @param dest Destination config
     * @param src Source object
     * @throws FieldAccessException Field access error
     */
    public static void serialize(ConfigurationSection dest, Object src) throws FieldAccessException {
        for (Field f: src.getClass().getFields()) {
            if (f.isAnnotationPresent(ConfigSerialization.Ignore.class)) continue;

            String key;
            if (f.isAnnotationPresent(ConfigSerialization.Name.class)) {
                key = f.getAnnotation(ConfigSerialization.Name.class).value();
            } else key = f.getName();

            f.trySetAccessible();

            try {
                if (f.isAnnotationPresent(ConfigSerialization.Serialize.class)) {
                    var sec = dest.createSection(f.getName());
                    serialize(sec, f.get(src));
                    dest.set(key, sec);
                } else {
                    dest.set(key, f.get(src));
                }
            } catch (IllegalAccessException ex) {
                throw new FieldAccessException(f, ex);
            }
        }
    }
}
