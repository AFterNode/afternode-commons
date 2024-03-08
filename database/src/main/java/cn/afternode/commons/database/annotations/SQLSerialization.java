package cn.afternode.commons.database.annotations;

import cn.afternode.commons.database.SQLTypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class SQLSerialization {
    /**
     * Markup for SQL table
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Table {
        String name();
    }

    /**
     * Markup for SQL column
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Column {
        /**
         * Column name
         */
        String name();

        /**
         * Column type, default to AUTO
         */
        SQLTypes type() default SQLTypes.AUTO;

        /**
         * Set this value cannot be null
         */
        boolean notNull() default false;

        /**
         * Mark as primary key, not supports TEXT type
         */
        boolean primaryKey() default false;

        /**
         * Serialize object to json with GSON, only works in AUTO type
         */
        boolean asJson() default false;
    }
}
