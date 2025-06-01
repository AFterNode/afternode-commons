package cn.afternode.commons.bukkit.kotlin.command.argument

import cn.afternode.commons.bukkit.BukkitResolver
import cn.afternode.commons.bukkit.kotlin.command.ArgumentResolver
import cn.afternode.commons.bukkit.kotlin.command.FlagResolver
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

/**
 * A vanilla entity selector argument
 */
class EntityArgument(override val key: String) : ArgumentResolver<List<Entity>> {
    override fun resolve(sender: CommandSender, current: String) =
        Bukkit.getServer().selectEntities(sender, current)
}

fun entityArgument(key: String) =
    EntityArgument(key)

class PlayerArgument(override val key: String, override val required: Boolean) : ArgumentResolver<Player>, FlagResolver<Player> {
    override fun resolve(sender: CommandSender, current: String): Player? =
        BukkitResolver.resolvePlayerOnline(current)

    override fun completion(sender: CommandSender, current: String): List<String> =
        Bukkit.getOnlinePlayers().parallelStream().map(Player::getName)
            .toList()
            .filter { it.startsWith(current) }
}

fun playerArgument(key: String, required: Boolean = false) =
    PlayerArgument(key, required)

class OfflinePlayerArgument(override val key: String, override val required: Boolean) : ArgumentResolver<OfflinePlayer>, FlagResolver<OfflinePlayer> {
    override fun resolve(
        sender: CommandSender,
        current: String
    ): OfflinePlayer? =
        BukkitResolver.resolvePlayer(current)

    override fun completion(sender: CommandSender, current: String): List<String> =
        listOf("uuid:")
}

fun offlinePlayerArgument(key: String, required: Boolean = false) =
    OfflinePlayerArgument(key, required)
