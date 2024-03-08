package cn.afternode.commons.bukkit.configurations;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigurationMerger {
    /**
     * Create a new YamlConfiguration, write src and dest into it
     * <br>
     * The contents of src are retained when the key is repeated
     * @param src Source configurations
     * @param dest Destination configurations
     * @return Merged
     */
    public static YamlConfiguration migrate(ConfigurationSection src, ConfigurationSection dest) {
        YamlConfiguration conf = new YamlConfiguration();

        for (String k: dest.getKeys(true)) conf.set(k, dest.get(k));
        for (String k: src.getKeys(true)) conf.set(k, src.get(k));

        return conf;
    }
}
