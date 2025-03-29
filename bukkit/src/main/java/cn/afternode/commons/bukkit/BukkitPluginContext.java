package cn.afternode.commons.bukkit;

import cn.afternode.commons.ReflectionError;
import cn.afternode.commons.bukkit.annotations.AutoRegistrationData;
import cn.afternode.commons.bukkit.annotations.RegisterCommand;
import cn.afternode.commons.bukkit.annotations.RegisterListener;
import cn.afternode.commons.bukkit.annotations.RegisterPluginCommand;
import cn.afternode.commons.bukkit.configurations.ConfigurationMerger;
import cn.afternode.commons.bukkit.gui.GuiManager;
import cn.afternode.commons.bukkit.message.CallbackCommand;
import cn.afternode.commons.bukkit.message.MessageBuilder;
import cn.afternode.commons.bukkit.report.PluginReport;
import cn.afternode.commons.localizations.ILocalizations;
import cn.afternode.commons.serialization.FieldAccessException;
import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BukkitPluginContext {
    private final Plugin plugin;

    private ComponentLike messageLinePrefix = Component.text().build();
    private boolean messageBuilderStyleStack = false;
    private ILocalizations localizations = null;
    private IAdventureLocalizations.LocalizeMode defaultLocalizeMode = IAdventureLocalizations.LocalizeMode.RAW;

    private CallbackCommand callbackCommand = null;
    private GuiManager guiManager = null;

    /**
     * @param plg Plugin instance will be used in registrations
     */
    public BukkitPluginContext(Plugin plg) {
        this.plugin = plg;
    }

    /**
     * Find classes with provided package name and register as CommandExecutor/TabExecutor
     * @param packageName Target package name
     * @throws RuntimeException Not a valid CommandExecutor/TabExecutor; Unable to create instance
     * @see RegisterPluginCommand
     */
    @Deprecated
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
     * @param packageName Package name for scanning
     * @see CommandMap
     * @see Command
     * @see RegisterCommand
     * @throws ReflectionError Error in reflections
     */
    @Deprecated
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
    @Deprecated
    public void registerListeners(String packageName) {
        Reflections ref = new Reflections(packageName);
        Set<Class<?>> classes =
                ref.get(Scanners.SubTypes.of(Scanners.TypesAnnotated.with(RegisterPluginCommand.class)).asClass());

        for (Class<?> c: classes) {
            try {
                if (!Listener.class.isAssignableFrom(c))
                    throw new IllegalArgumentException("%s is not assignable from org.bukkit.event.Listener".formatted(c.getName()));

                Constructor<? extends Listener> constructor = (Constructor<? extends Listener>) c.getDeclaredConstructor();
                constructor.setAccessible(true);

                Bukkit.getPluginManager().registerEvents(constructor.newInstance(), plugin);
            } catch (Throwable t) {
                throw new RuntimeException("Cannot register %s as an event listener".formatted(c.getName()), t);
            }
        }
    }

    /**
     * Register commands/listeners/plugin commands from generated resources with logging enabled
     * @throws IOException Error reading data resource
     * @throws RuntimeException Error registering
     */
    public void doAutoRegistration() throws IOException, IllegalArgumentException {
        this.doAutoRegistration(true);
    }

    /**
     * Register commands/listeners/plugin commands from generated resources
     * @param withLogs enable log output
     * @throws IOException Error reading data resource
     * @throws RuntimeException Error registering
     */
    public void doAutoRegistration(boolean withLogs) throws IOException, RuntimeException {
        final AutoRegistrationData data;
        ClassLoader loader = this.plugin.getClass().getClassLoader();
        try (InputStream i = Objects.requireNonNull(loader.getResourceAsStream(AutoRegistrationData.LOCATION), "Auto-registration data resource")) {
            data = new Gson().fromJson(new InputStreamReader(i), AutoRegistrationData.class);
        }

        PluginManager pm = Bukkit.getPluginManager();

        // commands
        if (!data.commands().isEmpty()) {
            for (String command : data.commands()) {
                try {
                    Bukkit.getServer().getCommandMap().register(this.plugin.getName(), (Command) BukkitPluginContext.getOrCreateInstance(loader.loadClass(command)));
                } catch (Throwable t) {
                    throw new IllegalArgumentException("Cannot register command" + command, t);
                }
            }
            if (withLogs)
                this.plugin.getSLF4JLogger().info("Registered {} commands", data.commands().size());
        }

        // listeners
        if (!data.listeners().isEmpty()) {
            for (String listener : data.listeners()) {
                try {
                    pm.registerEvents((Listener) BukkitPluginContext.getOrCreateInstance(loader.loadClass(listener)), this.plugin);
                } catch (Throwable t) {
                    throw new IllegalArgumentException("Cannot register listener" + listener, t);
                }
            }
            if (withLogs)
                this.plugin.getSLF4JLogger().info("Registered {} listeners", data.commands().size());
        }

        if (!data.pluginCommands().isEmpty()) {
            Map<String, String> pcm = data.pluginCommands();
            for (String pc : pcm.keySet()) {
                try {
                    String cmdName = pcm.get(pc);
                    PluginCommand command = Bukkit.getPluginCommand(cmdName);
                    if (command == null)
                        throw new NullPointerException("Unknown plugin command " + cmdName);

                    Object o = BukkitPluginContext.getOrCreateInstance(loader.loadClass(pc));

                    if (o instanceof CommandExecutor executor)
                        command.setExecutor(executor);
                    if (o instanceof TabCompleter completer)
                        command.setTabCompleter(completer);
                } catch (Throwable t) {
                    throw new IllegalArgumentException("Cannot plugin command" + pc, t);
                }
            }
            if (withLogs)
                this.plugin.getSLF4JLogger().info("Registered {} plugin commands", pcm.size());
        }
    }

    /**
     * Set plugin display name
     * @param name Target name
     * @see PluginDescriptionFile#getName()
     */
    public void setDisplayName(String name) {
        Field field = null;
        try {
            field = PluginDescriptionFile.class.getDeclaredField("name");
            field.trySetAccessible();
            field.set(plugin.getDescription(), name);
        } catch (NoSuchFieldException e) {
            throw new ReflectionError(PluginDescriptionFile.class, e);
        } catch (IllegalAccessException e) {
            throw new FieldAccessException(field, e);
        }
    }

    /**
     * Create MessageBuilder with localizations and prefix in this context
     * @param sender Sender passed to MessageBuilder
     * @return builder
     */
    public MessageBuilder message(CommandSender sender) {
        MessageBuilder mb = new MessageBuilder(localizations, messageLinePrefix, sender, messageBuilderStyleStack);
        mb.localizeMode(this.defaultLocalizeMode);
        return mb;
    }

    /**
     * Create MessageBuilder with localizations and prefix in this context
     * @return builder
     */
    public MessageBuilder message() {
        MessageBuilder mb = new MessageBuilder(localizations, messageLinePrefix, null, messageBuilderStyleStack);
        mb.localizeMode(this.defaultLocalizeMode);
        return mb;
    }

    /**
     * Set if use style stack for MessageBuilder in default
     * @param messageBuilderStyleStack State
     */
    public void setMessageBuilderStyleStack(boolean messageBuilderStyleStack) {
        this.messageBuilderStyleStack = messageBuilderStyleStack;
    }

    /**
     * Get status of default style stack for MessageBuilder
     * @return State
     */
    public boolean isMessageBuilderStyleStack() {
        return messageBuilderStyleStack;
    }

    /**
     * Get plugin of this context
     * @return Plugin
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Get message line prefix of this context
     * @return Message line prefix
     */
    public ComponentLike getMessageLinePrefix() {
        return messageLinePrefix;
    }

    /**
     * Set message line prefix of this context
     * @param messageLinePrefix Prefix
     */
    public void setMessageLinePrefix(ComponentLike messageLinePrefix) {
        this.messageLinePrefix = messageLinePrefix;
    }

    /**
     * Get localizations of this context
     * @return Localizations
     */
    public ILocalizations getLocalizations() {
        return localizations;
    }

    /**
     * Set localizations of this context
     * @param localizations Localizations
     */
    public void setLocalizations(ILocalizations localizations) {
        this.localizations = localizations;
    }

    /**
     * Get default localize mode of this context
     * @return Mode
     */
    public IAdventureLocalizations.LocalizeMode getDefaultLocalizeMode() {
        return defaultLocalizeMode;
    }

    /**
     * Set default localize mode of this context
     * @param defaultLocalizeMode Mode
     */
    public void setDefaultLocalizeMode(IAdventureLocalizations.LocalizeMode defaultLocalizeMode) {
        this.defaultLocalizeMode = defaultLocalizeMode;
    }

    /**
     * Load localizations from plugin resources
     * @param resource Resource path
     */
    public void loadLocalizations(String resource) throws IOException {
        YamlConfiguration loc;
        try (InputStream s = plugin.getResource(resource)) {
            if (s == null) {
                loc = new YamlConfiguration();
            } else
                loc = YamlConfiguration.loadConfiguration(new InputStreamReader(s));
        }
        this.localizations = new ConfigurationLocalizations(loc);
    }

    /**
     * Create PluginReport
     * @return Report
     */
    public PluginReport createReport() {
        return new PluginReport(this.plugin);
    }

    /**
     * Load configuration, save to data folder if not exists
     * @param path Resource and save path in data folder
     * @return Result
     * @throws IOException Error in resource loading or file IO
     * @throws NullPointerException Resource not found
     */
    public YamlConfiguration loadConfiguration(String path) throws IOException, NullPointerException {
        Path pth = plugin.getDataFolder().toPath().resolve(path);
        if (!Files.exists(pth)) {
            try (InputStream in = this.getResourceStrict(path)) {
                Files.copy(in, pth);
            }
        }
        return YamlConfiguration.loadConfiguration(Files.newBufferedReader(pth));
    }

    /**
     * Upgrade configuration, or save if not exists
     * @param path path Resource and save path in data folder
     * @return Loaded configuration
     * @throws IOException Error in resource loading or file IO
     * @throws NullPointerException Resource not found
     */
    public YamlConfiguration upgradeConfiguration(String path) throws IOException, NullPointerException {
        Path pth = plugin.getDataFolder().toPath().resolve(path);
        try (InputStream in = this.getResourceStrict(path)) {
            YamlConfiguration latest = YamlConfiguration.loadConfiguration(new InputStreamReader(in));
            if (!Files.exists(pth)) {
                Files.copy(in, pth);
                return latest;
            } else {
                Files.createFile(pth);
                YamlConfiguration current = YamlConfiguration.loadConfiguration(Files.newBufferedReader(pth));
                YamlConfiguration out = ConfigurationMerger.migrate(current, latest);
                out.save(pth.toFile());
                return out;
            }
        }
    }

    /**
     * Register a callback command for message component click event to bukkit
     * @return Command instance
     */
    public CallbackCommand createCallbackCommand() {
        if (this.callbackCommand != null)
            throw new IllegalStateException("Callback already registered for this context");

        this.callbackCommand = new CallbackCommand();
        Bukkit.getCommandMap().register(this.plugin.getName(), this.callbackCommand);
        return this.callbackCommand;
    }

    /**
     * Get current registered callback command
     * @return Command instance
     */
    public CallbackCommand getCallbackCommand() {
        return callbackCommand;
    }

    /**
     * Get or create a GuiManager instance
     * @return Current or new
     * @see GuiManager
     */
    public GuiManager getGuiManager() {
        if (this.guiManager == null) {
            this.guiManager = new GuiManager(this.plugin);
        }
        return guiManager;
    }

    public @NotNull InputStream getResourceStrict(String path) {
        InputStream in = this.plugin.getClass().getClassLoader().getResourceAsStream(path);
        if (in == null)
            in = this.plugin.getClass().getResourceAsStream(path);
        if (in == null)
            throw new NullPointerException(path + " @ resource");
        return in;
    }

    private static <T> T getOrCreateInstance(Class<T> type) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        try {
            Field instanceField = type.getDeclaredField("INSTANCE");
            if (type.isAssignableFrom(instanceField.getType()) && Modifier.isStatic(instanceField.getModifiers())) {  // kotlin object
                instanceField.trySetAccessible();
                return (T) instanceField.get(null);
            }
        } catch (NoSuchFieldException ignored) {}

        // create new instance
        Constructor<T> constructor = type.getDeclaredConstructor();
        return constructor.newInstance();
    }
}
