package cn.afternode.commons.database;

import cn.afternode.commons.database.annotations.SQLSerialization;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseUtils {
    private static final Gson gson = new Gson();

    /**
     * Get a MySQL JDBC URL
     * You must load class (e.g. com.mysql.cj.jdbc.Driver) by yourself
     */
    public static String getMySql(String host, int port, String database) {
        return "jdbc:mysql://%s:%s/%s".formatted(host, port, database);
    }

    /**
     * Convert field to SQL create table
     * @see cn.afternode.commons.database.annotations.SQLSerialization.Column
     */
    public static String toSQLCreateTable(Field fd) {
        if (!fd.isAnnotationPresent(SQLSerialization.Column.class)) throw new IllegalArgumentException("Column annotation not present");
        SQLSerialization.Column col = fd.getAnnotation(SQLSerialization.Column.class);

        String sql = "%s ".formatted(col.name());

        SQLTypes type = col.type();

        // Check type
        if (type == SQLTypes.TEXT && col.primaryKey()) throw new IllegalArgumentException("TEXT type cannot be primary key");

        // Convert type to SQL
        if (type == SQLTypes.AUTO) {
            Class<?> ft = fd.getType();
            if (ft == String.class) {
                type = SQLTypes.VARCHAR;
            } else if (col.asJson()) {
                type = SQLTypes.TEXT;
            } else if (ft == int.class || ft == Integer.class) {
                type = SQLTypes.INT;
            } else if (ft == long.class || ft == Long.class) {
                type = SQLTypes.BIGINT;
            } else if (ft == byte[].class) {
                type = SQLTypes.BLOB;
            } else {
                throw new RuntimeException("Type %s was not supported for AUTO type".formatted(ft.getName()));
            }
        }
        sql += type.getSql();

        // Set notNull
        if (col.notNull()) sql += " NOT NULL";

        // Set primaryKey
        if (col.primaryKey()) sql += " PRIMARY KEY";

        return sql;
    }

    /**
     * Convert object to SQL insert
     */
    public static String toSQLInsert(Object obj) {
        if (!obj.getClass().isAnnotationPresent(SQLSerialization.Table.class)) throw new IllegalArgumentException("%s has not SQLSerialization.Table annotation present".formatted(obj.getClass().getName()));
        SQLSerialization.Table anno = obj.getClass().getAnnotation(SQLSerialization.Table.class);

        String sql = "INSERT INTO `%s` ".formatted(anno.name());

        StringBuilder names = new StringBuilder();
        StringBuilder values = new StringBuilder();
        names.append(" (");
        values.append("(");
        for (Field f: obj.getClass().getFields()) {
            if (f.isAnnotationPresent(SQLSerialization.Column.class)) {
                SQLSerialization.Column col = f.getAnnotation(SQLSerialization.Column.class);
                if (!names.isEmpty()) names.append(", ");
                if (!values.isEmpty()) values.append(", ");
                names.append(col.name());
                values.append("?");
            }
        }
        names.append(") VALUES");
        values.append(")");
        sql += names.toString() + values;

        return sql.toString();
    }

    /**
     * Fill SQL insert prepared statement with object
     */
    public static void fillSQLInsert(Object obj, PreparedStatement stmt) throws IllegalAccessException, SQLException {
        int index = 1;

        for (Field f: obj.getClass().getFields()) {
            if (f.isAnnotationPresent(SQLSerialization.Column.class)) {
                SQLSerialization.Column col = f.getAnnotation(SQLSerialization.Column.class);

                f.setAccessible(true);
                Object fd = f.get(obj);
                switch (col.type()) {
                    case VARCHAR, TEXT -> stmt.setString(index, (String) fd);
                    case INT -> stmt.setInt(index, (int) fd);
                    case BIGINT -> stmt.setLong(index, (long) fd);
                    case BLOB -> stmt.setBlob(index, new ByteArrayInputStream((byte[]) fd));
                    case AUTO -> {
                        if (col.asJson()) {
                            stmt.setString(index, gson.toJson(fd));
                        } else {
                            Class<?> ft = f.getType();
                            if (ft == String.class) {
                                stmt.setString(index, (String) fd);
                            } else if (ft == int.class || ft == Integer.class) {
                                stmt.setInt(index, (int) fd);
                            } else if (ft == long.class || ft == Long.class) {
                                stmt.setLong(index, (long) fd);
                            } else if (ft == byte[].class) {
                                stmt.setBlob(index, new ByteArrayInputStream((byte[]) fd));
                            } else {
                                throw new RuntimeException("Type %s was not supported for AUTO type".formatted(ft.getName()));
                            }
                        }
                    }
                }

                index ++;
            }
        }
    }
}
