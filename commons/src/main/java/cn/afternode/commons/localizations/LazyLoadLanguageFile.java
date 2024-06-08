package cn.afternode.commons.localizations;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/**
 * Language file with lazy-load
 */
public class LazyLoadLanguageFile implements ILocalizations {
    private final Properties prop = new Properties();
    private final Function<String, Reader> loader;

    /**
     * Ignored localization keys
     */
    private final List<String> ignoreKeys = new ArrayList<>();

    /**
     * Ignore non-existing keys in localizations if it's in ignoreKeys
     * @see LazyLoadLanguageFile#ignoreKeys
     */
    private boolean autoIgnore = true;

    /**
     * @param loader Language file loader
     */
    public LazyLoadLanguageFile(Function<String, Reader> loader) {
        this.loader = loader;
    }

    /**
     * Get localizations from loaded prop
     * <br>
     * Load from loader if key not exists and not contains in ignore list (when autoIgnore enabled)
     * @param key Localization key
     * @return Result
     *
     * @see LazyLoadLanguageFile#setAutoIgnore(boolean)
     */
    @Override
    public String get(String key) {
        if (autoIgnore && ignoreKeys.contains(key)) return key;

        if (!prop.containsKey(key)) {
            try {
                Reader rd = loader.apply(key);
                if (rd != null) {
                    prop.load(rd);
                    rd.close();
                } else {
                    this.addIgnore(key);
                }
            } catch (IOException ex) {
                throw new RuntimeException("Unable to load localizations by key %s".formatted(key), ex);
            }
        }

        return prop.getProperty(key, key);
    }

    @Override
    public String get(String key, Map<String, Object> placeholders) {
        String result = get(key);
        for (String k: placeholders.keySet()) {
            result = result.replace("%" + k + "%", placeholders.get(k).toString());
        }
        return result;
    }

    /**
     * Set autoIgnore enabled
     * @param autoIgnore Target value
     * @see LazyLoadLanguageFile#autoIgnore
     */
    public void setAutoIgnore(boolean autoIgnore) {
        this.autoIgnore = autoIgnore;
    }

    /**
     * @return If autoIgnore is enabled
     */
    public boolean isAutoIgnore() {
        return autoIgnore;
    }

    /**
     * Add key to ignoreKeys
     * @param key Key to add
     * @see #ignoreKeys
     */
    public void addIgnore(String key) {
        if (autoIgnore) this.ignoreKeys.add(key);
    }

    /**
     * @param key Key to query
     * @return If ignoreKeys contains this key
     * @see #ignoreKeys
     */
    public boolean isIgnore(String key) {
        return this.ignoreKeys.contains(key);
    }

    /**
     * Remove key from ignoreKeys
     * @return If this ignoreKeys contained the specified element
     * @see #ignoreKeys
     * @see List#remove(Object)
     */
    public boolean removeIgnore(String key) {
        return this.ignoreKeys.remove(key);
    }

    /**
     * Clear ignoreKeys
     * @see #ignoreKeys
     * @see List#clear()
     */
    public void clearIgnore() {
        this.ignoreKeys.clear();
    }
}
