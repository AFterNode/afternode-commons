package cn.afternode.commons.bukkit.kotlin

import cn.afternode.commons.bukkit.message.TabBuilder
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission

/**
 * Create command suggestion (Tab completion)
 */
fun commandSuggestion(sender: CommandSender? = null, block: TabBuilder.() -> Unit): MutableList<String> {
    val builder = TabBuilder(sender)
    block(builder)
    return builder.build()
}
