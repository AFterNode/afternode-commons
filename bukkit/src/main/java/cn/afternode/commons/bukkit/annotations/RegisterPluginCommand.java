package cn.afternode.commons.bukkit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for registerPluginCommands
 * @see cn.afternode.commons.bukkit.Registration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterPluginCommand {
    /**
     * Target command name, must be defined in plugin.yml
     */
    String name();
}
