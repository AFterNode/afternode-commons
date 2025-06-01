package cn.afternode.commons.bukkit.kotlin.command.argument

import cn.afternode.commons.bukkit.kotlin.command.ArgumentResolver
import cn.afternode.commons.bukkit.kotlin.command.FlagResolver
import org.bukkit.command.CommandSender

private val booleanConstants = listOf("true", "false")

/**
 * Boolean argument resolver with text or digit support
 */
class BooleanArgument(override val key: String, override val required: Boolean) : ArgumentResolver<Boolean>, FlagResolver<Boolean> {
    override fun resolve(sender: CommandSender, current: String): Boolean =
        current.lowercase().toBooleanStrictOrNull() ?: (current.toIntOrNull() != 0)

    override fun completion(sender: CommandSender, current: String): List<String> =
        booleanConstants
}

fun booleanArgument(key: String, required: Boolean = false) =
    BooleanArgument(key, required)
