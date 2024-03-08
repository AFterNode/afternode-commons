package cn.afternode.commons.database;

import cn.afternode.commons.database.annotations.SQLSerialization;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDatabaseManager {
    void setup(String url, String username, String password);

    /**
     * Close current database
     */
    void close();

    /**
     * Create new connection
     */
    Connection getConnection() throws SQLException;

    /**
     * Create a table with provided class
     * @param table Table data class, must have SQLSerialization.Table annotation
     * @see SQLSerialization.Table
     */
    void newTable(Class<?> table) throws SQLException;

    /**
     * Insert object into created table
     * @param obj Data class object, must have SQLSerialization.Table annotation
     * @see SQLSerialization.Table
     */
    void insert(Object obj) throws SQLException;
}
