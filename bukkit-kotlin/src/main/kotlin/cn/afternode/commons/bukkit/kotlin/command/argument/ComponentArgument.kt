package cn.afternode.commons.bukkit.kotlin.command.argument

import cn.afternode.commons.bukkit.kotlin.command.ArgumentResolver
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.CommandSender
import kotlin.getValue

private val legacy by lazy { LegacyComponentSerializer.legacyAmpersand() }

/**
 * Legacy to Adventure component serializer with long text support
 * @see LegacyComponentSerializer.legacyAmpersand
 * @param serializer Custom serializer
 */
class LegacyComponentArgument(override val key: String, private val serializer: LegacyComponentSerializer = legacy) : ArgumentResolver<Component> {
    override fun resolve(
        sender: CommandSender,
        current: String
    ): Component = legacy.deserialize(current)
}

fun legacyComponentArgument(key: String, serializer: LegacyComponentSerializer = legacy) =
    LegacyComponentArgument(key, serializer)
