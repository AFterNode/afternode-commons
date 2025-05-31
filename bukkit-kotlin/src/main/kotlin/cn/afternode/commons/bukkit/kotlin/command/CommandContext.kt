package cn.afternode.commons.bukkit.kotlin.command

import org.bukkit.command.CommandSender

data class CommandContext(
    val sender: CommandSender,
    val args: Map<String, Any>
) {
    operator fun <T> get(key: String) =
        try {
            this.args[key] as T
        } catch (e: ClassCastException) {
            null
        }
}
