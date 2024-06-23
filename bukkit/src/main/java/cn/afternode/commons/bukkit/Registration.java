package cn.afternode.commons.bukkit;

import cn.afternode.commons.ReflectionError;
import cn.afternode.commons.bukkit.annotations.RegisterCommand;
import cn.afternode.commons.bukkit.annotations.RegisterListener;
import cn.afternode.commons.bukkit.annotations.RegisterPluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * @deprecated Use BukkitPluginContext
 * @see BukkitPluginContext
 */
@Deprecated(
        forRemoval = true
)
public class Registration {
    private final Plugin plugin;

    /**
     * @param plg Plugin instance will be used in registrations
     */
    @Deprecated(forRemoval = true)
    public Registration(Plugin plg) {
        this.plugin = plg;
    }

    /**
     * Find classes with provided package name and register as CommandExecutor/TabExecutor
     * @param packageName Target package name
     * @throws RuntimeException Not a valid CommandExecutor/TabExecutor; Unable to create instance
     * @see RegisterPluginCommand
     */
    @Deprecated(forRemoval = true)
    public void registerPluginCommands(String packageName) throws RuntimeException {
        Reflections ref = new Reflections(packageName);
        Set<Class<?>> classes =
                ref.get(Scanners.SubTypes.of(Scanners.TypesAnnotated.with(RegisterPluginCommand.class)).asClass());

        for (Class<?> c: classes) {
            try {
                if (
                        !CommandExecutor.class.isAssignableFrom(c)
                                && !TabCompleter.class.isAssignableFrom(c)
                                && !TabExecutor.class.isAssignableFrom(c)
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
     * Find classes with provided package name and register as Command, target class must extend org.bukkit.command.Command
     * <br>
     * This method will register commands through CommandMap
     * @param packageName Package for searching
     * @see org.bukkit.command.CommandMap
     * @see org.bukkit.command.Command
     * @see cn.afternode.commons.bukkit.annotations.RegisterCommand
     * @throws cn.afternode.commons.ReflectionError Error in reflections
     */
    @Deprecated(forRemoval = true)
    public void registerCommands(String packageName) {
        Reflections ref = new Reflections(packageName);
        Set<Class<?>> classes =
                ref.get(Scanners.SubTypes.of(Scanners.TypesAnnotated.with(RegisterCommand.class)).asClass());

        SimpleCommandMap map = BukkitReflections.getCommandMap();

        for (Class<?> c: classes) {
            if (!Command.class.isAssignableFrom(c)) throw new IllegalArgumentException("%s is not a valid Command class but a RegisterCommand annotation was present".formatted(c.getName()));

            Constructor<Command> cons;
            try {
                cons = (Constructor<Command>) c.getDeclaredConstructor();
            } catch (NoSuchMethodException e){
                throw new ReflectionError(c, e);
            }
            cons.trySetAccessible();
            Command inst;
            try {
                inst = cons.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new ReflectionError(cons, c, e);
            }

            map.register(plugin.getName(), inst);
        }
    }

    /**
     * Find classes with provided package name and register as event listener
     * @param packageName Target package name
     * @see RegisterListener
     */
    @Deprecated(forRemoval = true)
    public void registerListeners(String packageName) {
        Reflections ref = new Reflections(packageName);
        Set<Class<?>> classes =
                ref.get(Scanners.SubTypes.of(Scanners.TypesAnnotated.with(RegisterPluginCommand.class)).asClass());

        for (Class<?> c: classes) {
            try {
                if (!Listener.class.isAssignableFrom(c))
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

    @Deprecated(forRemoval = true)
    public Plugin getPlugin() {
        return plugin;
    }
}
