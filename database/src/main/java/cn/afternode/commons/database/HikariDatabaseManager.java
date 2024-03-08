package cn.afternode.commons.database;

import cn.afternode.commons.database.annotations.SQLSerialization;
import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * ConnectionProvider with <a href="https://github.com/brettwooldridge/HikariCP">HikariCP</a>
 */
public class HikariDatabaseManager implements IDatabaseManager {
    public static Builder builder() {
        return new Builder();
    }

    private final HikariDataSource source;
    private final ConnectionMonitor monitor;
    private final Gson gson = new Gson();

    HikariDatabaseManager(HikariDataSource source, long connectionLifetime) {
        this.source = source;
        this.monitor = new ConnectionMonitor("Hikari", connectionLifetime);
    }

    @Override
    public void setup(String url, String username, String password) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void close() {
        if (!source.isClosed()) source.close();
        if (monitor.isAlive()) monitor.close();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    /**
     * @param table Table data class, must have TableSerialization.Name annotation
     */
    @Override
    public void newTable(Class<?> table) throws SQLException {
        if (!table.isAnnotationPresent(SQLSerialization.Table.class)) throw new IllegalArgumentException("%s has not SQLSerialization.Table annotation present".formatted(table.getName()));
        SQLSerialization.Table anno = table.getAnnotation(SQLSerialization.Table.class);

        String sql = "CREATE TABLE IF NOT EXISTS `%s` ".formatted(anno.name());

        // Walk fields
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Field f: table.getFields()) {
            if (f.isAnnotationPresent(SQLSerialization.Column.class)) {
                if (!sb.isEmpty()) sb.append(", ");
                sb.append(DatabaseUtils.toSQLCreateTable(f));
            }
        }
        sb.append(")");
        sql += sb.toString();

        try (var stmt = getConnection().createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public void insert(Object obj) throws SQLException {
        String sql = DatabaseUtils.toSQLInsert(obj);

        try (var stmt = getConnection().prepareStatement(sql)) {
            try {
                DatabaseUtils.fillSQLInsert(obj, stmt);
            } catch (Throwable t) {
                throw new SQLException("Cannot fill SQL insert", t);
            }
            stmt.execute();
        }
    }

    public static class Builder {
        private long connectionLifetime = 10000;
        private int maxSize = 50;
        private String name;

        private String url = null;
        private String username = null;
        private String password = null;

        private final Map<String, Object> prop = new HashMap<>();

        Builder() {}

        public Builder connectionLifetime(long value) {
            this.connectionLifetime = value;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder mysql(String host, int port, String name) {
            this.url = DatabaseUtils.getMySql(host, port, name);
            return this;
        }

        public Builder property(String key, Object value) {
            this.prop.put(key, value);
            return this;
        }

        public Builder maximumPoolSize(int size) {
            this.maxSize = size;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder credentials(String username, String password) {
            this.username = username;
            this.password = password;
            return this;
        }

        public HikariDatabaseManager build() {
            if (url == null) throw new NullPointerException("JDBC url not present");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            if (username != null) config.setUsername(username);
            if (password != null) config.setPassword(password);

            config.setMaximumPoolSize(maxSize);
            config.setMaxLifetime(connectionLifetime);
            config.setPoolName(name);

            for (String k: prop.keySet()) config.addDataSourceProperty(k, prop.get(k));

            HikariDataSource hds = new HikariDataSource(config);
            return new HikariDatabaseManager(hds, connectionLifetime);
        }
    }
}
