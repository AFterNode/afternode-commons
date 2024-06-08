package cn.afternode.commons;

import java.util.Map;

public interface ILocalizations {
    /**
     * Get localization without any placeholder
     * @param key Localization key
     * @return Result localizations, or provided key if not found
     */
    String get(String key);

    /**
     * Get localizations with specified placeholders
     * @param key Localization key
     * @param placeholders Placeholders
     * @return Result localizations with placeholders applied, or provided key if not found
     */
    String get(String key, Map<String, Object> placeholders);
}
