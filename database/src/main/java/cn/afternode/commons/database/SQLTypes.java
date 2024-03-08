package cn.afternode.commons.database;

/**
 * SQL Types
 */
public enum SQLTypes {
    /**
     * Auto detection
     */
    AUTO(""),

    /**
     * Varchar with max length 255, default for java.lang.String
     */
    VARCHAR("varchar(255)"),

    /**
     * Long text
     */
    TEXT("text"),

    /**
     * Integer, default for java.lang.Integer
     */
    INT("int"),

    /**
     * Big integer, default for java.lang.Long
     */
    BIGINT("bigint"),

    /**
     * Blob, default for byte[]
     */
    BLOB("blob")
    ;

    private final String sql;

    SQLTypes(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
