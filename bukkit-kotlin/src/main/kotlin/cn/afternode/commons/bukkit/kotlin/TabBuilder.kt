package cn.afternode.commons.bukkit.kotlin

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission

class TabBuilder(
    val sender: CommandSender? = null,
    val ignoreCase: Boolean = false
) {
    private val list = ArrayList<String>()

    fun add(vararg items: String, prefix: String = "", ignoreCase: Boolean = this.ignoreCase): TabBuilder {
        items.forEach {
            if (it.startsWith(prefix, ignoreCase)) list.add(it)
        }
        return this
    }

    fun players(prefix: String = "", ignoreCase: Boolean = this.ignoreCase): TabBuilder =
        players { it.startsWith(prefix, ignoreCase) }

    fun players(filter: (String) -> Boolean): TabBuilder {
        Bukkit.getOnlinePlayers().forEach {
            if (filter(it.name)) this.list.add(it.name)
        }
        return this
    }

    fun worlds(prefix: String, ignoreCase: Boolean = this.ignoreCase): TabBuilder =
        worlds { it.startsWith(prefix, ignoreCase) }

    fun worlds(filter: (String) -> Boolean): TabBuilder {
        Bukkit.getWorlds().forEach {
            if (filter(it.name)) this.list.add(it.name)
        }
        return this
    }

    /**
     * Add completions if sender has specified permission
     * @param permission Target permission
     * @param items Items to add
     * @see sender
     */
    fun permission(permission: String, vararg items: String, prefix: String = ""): TabBuilder {
        if (sender?.hasPermission(permission) ?: throw NullPointerException("No sender provided"))
            add(*items, prefix)
        return this
    }

    /**
     * Add completions if sender has specified permission
     * @param permission Target permission
     * @param items Items to add
     * @see sender
     */
    fun permission(permission: Permission, vararg items: String, prefix: String = ""): TabBuilder {
        if (sender?.hasPermission(permission) ?: throw NullPointerException("No sender provided"))
            add(*items, prefix)
        return this
    }

    /**
     * Remove completions if not has prefix
     * @param prefix Target prefix
     */
    fun filter(prefix: String): TabBuilder {
        list.removeIf { !it.startsWith(prefix, ignoreCase) }
        return this
    }

    /**
     * Call removeIf on completions
     * @param filter Prediction
     */
    fun filter(filter: (String) -> Boolean): TabBuilder {
        list.removeIf(filter)
        return this
    }

    fun build() = list.toMutableList()
}

/**
 * Create command suggestion
 *
 * Moved
 * @see commandSuggestion
 */
@Deprecated("Moved", ReplaceWith("commandSuggestion(sender, ignoreCase, block)"))
fun tabComplete(sender: CommandSender? = null, ignoreCase: Boolean = false, block: TabBuilder.() -> Unit): MutableList<String> = commandSuggestion(sender, ignoreCase, block)

/**
 * Create command suggestion (Tab completion)
 */
fun commandSuggestion(sender: CommandSender? = null, ignoreCase: Boolean = false, block: TabBuilder.() -> Unit): MutableList<String> {
    val builder = TabBuilder(sender, ignoreCase)
    block(builder)
    return builder.build()
}
