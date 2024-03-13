package cn.afternode.commons.bukkit;

import cn.afternode.commons.ReflectionError;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;

public class BukkitReflections {
    /**
     * Get commandMap in SimplePluginManager
     * @return Result SimpleCommandMap
     * @throws ReflectionError Error in reflections
     */
    public static SimpleCommandMap getCommandMap() {
        Field f;
        try {
            f = SimplePluginManager.class.getDeclaredField("commandMap");
        } catch (NoSuchFieldException e) {
            throw new ReflectionError(SimplePluginManager.class, e);
        }
        f.trySetAccessible();

        try {
            return (SimpleCommandMap) f.get(Bukkit.getPluginManager());
        } catch (IllegalAccessException e) {
            throw new ReflectionError(f, SimplePluginManager.class, e);
        }
    }
}
