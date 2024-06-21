package cn.afternode.commons.bukkit.kotlin

import cn.afternode.commons.localizations.ILocalizations
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

class BukkitKtContext(val plugin: Plugin) {
    /**
     * Line prefix for MessageBuilder
     * @see MessageBuilder
     * @see BukkitKtContext.message
     */
    var messageLinePrefix = Component.text()

    /**
     * Localizations for MessageBuilder
     * @see MessageBuilder
     * @see BukkitKtContext.message
     */
    var localization: ILocalizations? = null

    /**
     * Create commandSuggestion
     * @see commandSuggestion
     */
    fun tab(sender: CommandSender? = null, ignoreCase: Boolean = false, block: TabBuilder.() -> Unit) =
        commandSuggestion(sender, ignoreCase, block)

    /**
     * Wrapped message builder using linePrefix and localizations in current context
     * @see BukkitKtContext.messageLinePrefix
     * @see BukkitKtContext.localization
     * @see MessageBuilder
     */
    fun message(block: MessageBuilder.() -> Unit) = message(linePrefix = messageLinePrefix, locale = localization, block = block)
}