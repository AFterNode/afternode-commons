package cn.afternode.commons.bukkit.configurations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ConfigSerialization {
    /**
     * Ignore field from serialization and deserialization
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Ignore {}

    /**
     * Specify key in serialization and deserialization
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Name {
        String value();
    }

    /**
     * Serialize object in a ConfigurationSection
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Serialize { }
}
