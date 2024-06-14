package cn.afternode.commons.bukkit.kotlin

import cn.afternode.commons.bukkit.BukkitReflections
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

abstract class BaseCommand(
    name: String,
    description: String = ""
): Command(name) {
    init {
        this.description = description
    }

    final override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
        exec(sender, *args)
        return true
    }

    final override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): MutableList<String> {
        return tab(sender, *args)
    }

    /**
     * @see Command.execute
     */
    abstract fun exec(sender: CommandSender, vararg args: String)

    /**
     * @see Command.tabComplete
     */
    open fun tab(sender: CommandSender, vararg args: String): MutableList<String> = mutableListOf()

    /**
     * Register this command through CommandMap
     * @param fallbackPrefix A prefix which is prepended to each command with a ':' one or more times to make the command unique
     * @see org.bukkit.command.CommandMap.register(java.lang.String, org.bukkit.command.Command)
     */
    fun register(fallbackPrefix: String) = BukkitReflections.getCommandMap().register(fallbackPrefix, this)
}