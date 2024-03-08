package cn.afternode.commons.bukkit.configurations;

import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ConfigurationsSerializer {
    /**
     * Deserialize configurations into provided object
     * @param config Source configuration
     * @param object Target object
     * @return Deserialized object
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
            } catch (Throwable t) {
                throw new RuntimeException("Error processing field %s".formatted(f.getName()), t);
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
     */
    public static <T> T deserialize(ConfigurationSection config, Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);

            T instance = constructor.newInstance();

            if (config == null) return instance;
            return deserialize(config, instance);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to deserialize configuration");
        }
    }

    /**
     * Serialize object into ConfigurationSection
     * @param dest Destination config
     * @param src Source object
     */
    public static void serialize(ConfigurationSection dest, Object src) {
        for (Field f: src.getClass().getFields()) {
            try {
                if (f.isAnnotationPresent(ConfigSerialization.Ignore.class)) continue;

                String key;
                if (f.isAnnotationPresent(ConfigSerialization.Name.class)) {
                    key = f.getAnnotation(ConfigSerialization.Name.class).value();
                } else key = f.getName();

                f.trySetAccessible();

                if (f.isAnnotationPresent(ConfigSerialization.Serialize.class)) {
                    var sec = dest.createSection(f.getName());
                    serialize(sec, f.get(src));
                    dest.set(f.getName(), sec);
                } else {
                    dest.set(f.getName(), f.get(src));
                }
            } catch (Throwable t) {
                throw new RuntimeException("Error processing field %s".formatted(f.getName()), t);
            }
        }
    }
}
