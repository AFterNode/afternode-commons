package cn.afternode.commons.binary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BinarySerialize {
    /**
     * Version marker for binary deserialization
     * <br>
     * If version mismatched, an error will be thrown
     * @return version
     */
    short version() default 0;

    /**
     * Exclude field or class from binary serialization
     */
    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Exclude {}
}
