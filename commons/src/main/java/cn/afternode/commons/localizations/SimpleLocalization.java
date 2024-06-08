package cn.afternode.commons.localizations;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Simple localizations using properties file
 */
public class SimpleLocalization implements ILocalizations {
    private final Properties prop = new Properties();

    /**
     * Load localizations from resources
     * @param loader Resource ClassLoader
     * @param resourcePath Resource path
     * @throws IOException Properties.load() failed
     */
    public SimpleLocalization(ClassLoader loader, String resourcePath) throws IOException {
        try (InputStream stream = Objects.requireNonNull(loader.getResourceAsStream(resourcePath), () -> "Cannot find resource %s".formatted(resourcePath))) {
            prop.load(stream);
        }
    }

    /**
     * Load localizations from Map
     * @param map Source
     */
    public SimpleLocalization(Map<String, String> map) {
        prop.putAll(map);
    }

    /**
     * Load localizations from InputStream
     * @param stream Source
     * @throws IOException Properties.load() failed
     */
    public SimpleLocalization(InputStream stream) throws IOException {
        prop.load(stream);
    }

    /**
     * Load localizations from string
     * @param prop Properties string
     * @throws IOException Properties.load() failed
     */
    public SimpleLocalization(String prop) throws IOException {
        this.prop.load(new StringReader(prop));
    }

    /**
     * Clear all keys
     */
    public void clear() {
        prop.clear();
    }

    /**
     * Put/Replace localizations
     */
    public void put(String key, String value) {
        prop.put(key, value);
    }

    @Override
    public String get(String key) {
        return prop.getProperty(key, key);
    }

    @Override
    public String get(String key, Map<String, Object> placeholders) {
        String get = get(key);
        for (String k: placeholders.keySet()) {
            get = get.replace("%" + get + "%", placeholders.get(k).toString());
        }
        return get;
    }
}
