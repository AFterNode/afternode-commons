package cn.afternode.commons.bukkit.kotlin.command.argument

import cn.afternode.commons.bukkit.kotlin.command.ArgumentResolver
import cn.afternode.commons.bukkit.kotlin.command.FlagResolver
import cn.afternode.commons.bukkit.kotlin.command.ParsedArguments
import org.bukkit.command.CommandSender

class EnumerationArgument<E : Enum<*>>(
    override val key: String,
    override val required: Boolean,
    type: Class<E>
) : ArgumentResolver<E>, FlagResolver<E> {
    private val valueIndexed = type.enumConstants
    private val valueKv = valueIndexed.associateBy { it.name.lowercase() }
    private val values = valueKv.values.toList()
    private val completions = values.map { it.name }

    override fun resolve(sender: CommandSender, current: String): E? {
        // resolve named
        val name = current.lowercase()
        if (name in valueKv)
            return valueKv[name]!!

        // resolve index
        val index = current.toIntOrNull()
            ?: return null
        return if (index >= 0) {
            values.getOrNull(index)
        } else null
    }

    override fun resolve(
        sender: CommandSender,
        args: ParsedArguments
    ): E? = if (this.key in args.flags)
        this.resolve(sender, args.flags[this.key]!!)
    else null

    override fun completion(sender: CommandSender, current: String): List<String> =
        completions
}

fun <E : Enum<*>> enumerationArgument(key: String, type: Class<E>, required: Boolean = false) =
    EnumerationArgument(key, required, type)
