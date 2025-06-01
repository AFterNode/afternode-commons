package cn.afternode.commons.bukkit.kotlin.command.argument

import cn.afternode.commons.bukkit.kotlin.command.ArgumentResolver
import cn.afternode.commons.bukkit.kotlin.command.FlagResolver
import cn.afternode.commons.bukkit.kotlin.command.ParsedArguments
import org.bukkit.command.CommandSender

class DoubleArgument(override val key: String, override val required: Boolean) : ArgumentResolver<Double>, FlagResolver<Double> {
    override fun resolve(sender: CommandSender, current: String): Double? =
        current.toDoubleOrNull()
}

fun doubleArgument(key: String, required: Boolean = false) =
    DoubleArgument(key, required)

class FloatArgument(override val key: String, override val required: Boolean) : ArgumentResolver<Float>, FlagResolver<Float> {
    override fun resolve(sender: CommandSender, current: String): Float? =
        current.toFloat()
}

fun floatArgument(key: String, required: Boolean = false) =
    FloatArgument(key, required)

class IntArgument(override val key: String, override val required: Boolean) : ArgumentResolver<Int>, FlagResolver<Int> {
    override fun resolve(sender: CommandSender, current: String): Int? =
        current.toInt()
}

fun intArgument(key: String, required: Boolean = false) =
    IntArgument(key, required)
