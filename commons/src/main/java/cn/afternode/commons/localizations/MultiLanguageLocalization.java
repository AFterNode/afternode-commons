package cn.afternode.commons.localizations;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Localizations provider with properties format
 *
 * @see Properties
 */
public class MultiLanguageLocalization implements ILocalizations {
    private final String prefix;
    private final Properties prop = new Properties();
    private final ClassLoader loader;

    /**
     *
     * @param prefix Resource name prefix
     * @param loader ClassLoader for getResourceAsStream
     */
    public MultiLanguageLocalization(String prefix, ClassLoader loader) {
        this.prefix = prefix;
        this.loader = loader;
    }

    /**
     * Create and load default localizations
     * @param prefix Resource name prefix
     * @param loader ClassLoader for getResourceAsStream
     * @param defaultId Default localizations ID
     * @throws IOException Error in Properties.load
     * @see #loadLocalizations(String)
     * @see #MultiLanguageLocalization(String, ClassLoader)
     */
    public MultiLanguageLocalization(String prefix, ClassLoader loader, String defaultId) throws IOException {
        this(prefix, loader);
        loadLocalizations(defaultId);
    }

    /**
     * Load default locale in current environment with ISO 639 code
     * @throws IOException Error in Properties.load
     * @see Locale#getLanguage()
     * @see #loadLocalizations(String)
     */
    public void loadDefault() throws IOException {
        loadLocalizations(Locale.getDefault().getLanguage());
    }

    /**
     * Load localizations with ID
     * @param id Localization ID in resources
     * @throws IOException Error in Properties.load
     * @see Properties#load(InputStream)
     */
    public void loadLocalizations(String id) throws IOException {
        prop.load(loader.getResourceAsStream("%s_%s.properties".formatted(prefix, id)));
    }

    /**
     * Clear all loaded localizations
     */
    public void clear() {
        this.prop.clear();
    }

    /**
     * Get localization without any placeholder
     * @param key Localization key
     * @return Result localizations, or provided key if not found
     */
    public String get(String key) {
        return prop.getProperty(key, key);
    }

    /**
     * Get localizations with specified placeholders
     * @param key Localization key
     * @param placeholders Placeholders
     * @return Result localizations with placeholders applied, or provided key if not found
     */
    public String get(String key, Map<String, Object> placeholders) {
        String get = get(key);
        for (String k: placeholders.keySet()) {
            get = get.replace("%" + get + "%", placeholders.get(k).toString());
        }
        return get;
    }
}
