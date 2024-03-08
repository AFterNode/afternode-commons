package cn.afternode.commons.bukkit;

import cn.afternode.commons.Filter;
import cn.afternode.commons.bukkit.annotations.RegisterListener;
import cn.afternode.commons.bukkit.annotations.RegisterPluginCommand;
import cn.afternode.commons.reflect.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.security.ProtectionDomain;

public class Registration {
    private final Plugin plugin;

    /**
     * @param plg Plugin instance will be used in registrations
     */
    public Registration(Plugin plg) {
        this.plugin = plg;
    }

    /**
     * Find classes with provided package name and register as CommandExecutor/TabExecutor
     * @param packageName Target package name
     * @throws IOException Error while looking for classes
     * @throws RuntimeException Not a valid CommandExecutor/TabExecutor; Unable to create instance
     * @see RegisterPluginCommand
     * @see Reflections#findClassByAnnotation(Class, ProtectionDomain, Filter)
     */
    public void registerPluginCommands(String packageName) throws IOException, RuntimeException {
        Class<?>[] classes = Reflections.findClassByAnnotation(RegisterPluginCommand.class,
                plugin.getClass().getProtectionDomain(),
                (it) -> it.startsWith(packageName));

        for (Class<?> c: classes) {
            try {
                if (
                        !c.isAssignableFrom(CommandExecutor.class)
                                && !c.isAssignableFrom(TabCompleter.class)
                                && !c.isAssignableFrom(TabExecutor.class)
                ) throw new IllegalArgumentException("%s is not a valid CommandExecutor/TabExecutor but a RegisterPluginCommand annotation was present".formatted(c.getName()));

                RegisterPluginCommand anno = c.getAnnotation(RegisterPluginCommand.class);
                PluginCommand cmd = Bukkit.getPluginCommand(anno.name());
                if (cmd == null) throw new NullPointerException("Cannot find plugin command %s".formatted(anno.name()));

                Constructor<?> constructor = c.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object instance = constructor.newInstance();

                if (instance instanceof CommandExecutor) cmd.setExecutor((CommandExecutor) instance);
                if (instance instanceof TabCompleter) cmd.setTabCompleter((TabCompleter) instance);
            } catch (Throwable t) {
                throw new RuntimeException("Cannot register %s as a plugin command".formatted(c.getName()), t);
            }
        }
    }

    /**
     * Find classes with provided package name and register as event listener
     * @param packageName Target package name
     * @throws IOException Error while looking for class
     * @throws RuntimeException Not a valid listener; Cannot create instance
     * @see RegisterListener
     * @see Reflections#findClassByAnnotation(Class, ProtectionDomain, Filter)
     */
    public void registerListeners(String packageName) throws IOException {
        Class<?>[] classes = Reflections.findClassByAnnotation(RegisterListener.class,
                plugin.getClass().getProtectionDomain(),
                (it) -> it.startsWith(packageName));

        for (Class<?> c: classes) {
            try {
                if (!c.isAssignableFrom(Listener.class))
                    throw new IllegalArgumentException("%s is not assignable from org.bukkit.event.Listener".formatted(c.getName()));

                RegisterListener anno = c.getAnnotation(RegisterListener.class);

                Constructor<? extends Listener> constructor = (Constructor<? extends Listener>) c.getDeclaredConstructor();
                constructor.setAccessible(true);

                Bukkit.getPluginManager().registerEvents(constructor.newInstance(), plugin);
            } catch (Throwable t) {
                throw new RuntimeException("Cannot register %s as an event listener".formatted(c.getName()), t);
            }
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
