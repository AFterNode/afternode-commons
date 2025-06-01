package cn.afternode.commons.bukkit.kotlin.command.argument

import cn.afternode.commons.bukkit.kotlin.command.ArgumentResolver
import cn.afternode.commons.bukkit.kotlin.command.FlagResolver
import org.bukkit.command.CommandSender
import java.net.URL

/**
 * A plain text argument with long text support
 */
class PlainTextArgument(
    override val key: String,
    val completions: List<String>,
    override val required: Boolean
) : ArgumentResolver<String>, FlagResolver<String> {
    override fun resolve(sender: CommandSender, current: String): String? = current

    override fun completion(sender: CommandSender, current: String): List<String> =
        this.completions
}

fun plainTextArgument(key: String, vararg completions: String, required: Boolean = false) =
    PlainTextArgument(key, completions.toList(), required)

class URLArgument(override val key: String, override val required: Boolean) : ArgumentResolver<URL>, FlagResolver<URL> {
    override fun resolve(sender: CommandSender, current: String): URL? =
        try {
            URL(current)
        } catch (_: IllegalArgumentException) {
            null
        }
}

fun urlArgument(key: String, required: Boolean) =
    URLArgument(key, required)
