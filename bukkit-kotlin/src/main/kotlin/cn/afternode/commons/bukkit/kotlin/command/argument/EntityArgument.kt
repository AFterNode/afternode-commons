package cn.afternode.commons.bukkit.kotlin.command.argument

import cn.afternode.commons.bukkit.BukkitResolver
import cn.afternode.commons.bukkit.kotlin.command.ArgumentResolver
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

class PlayerArgument(override val key: String) : ArgumentResolver<Player> {
    override fun resolve(sender: CommandSender, current: String): Player? =
        BukkitResolver.resolvePlayerOnline(current)

    override fun completion(sender: CommandSender, current: String): List<String> =
        Bukkit.getOnlinePlayers().parallelStream().map(Player::getName)
            .toList()
}

fun playerArgument(key: String) =
    PlayerArgument(key)

class OfflinePlayerArgument(override val key: String) : ArgumentResolver<OfflinePlayer> {
    override fun resolve(
        sender: CommandSender,
        current: String
    ): OfflinePlayer? =
        BukkitResolver.resolvePlayer(current)
}

fun offlinePlayerArgument(key: String) =
    OfflinePlayerArgument(key)
