package cn.afternode.commons.bukkit.kotlin.command.argument

import cn.afternode.commons.bukkit.kotlin.command.ArgumentResolver
import cn.afternode.commons.bukkit.kotlin.command.FlagResolver
import cn.afternode.commons.bukkit.kotlin.command.ParsedArguments
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity

/**
 * A world name argument with sender's world as default
 *
 * @param key Key of this argument/flag
 * @param required treat this flag as required
 */
class WorldArgument(override val key: String, override val required: Boolean) : ArgumentResolver<World>, FlagResolver<World> {
    private val worlds = Bukkit.getWorlds().map(World::getName)

    override fun resolve(sender: CommandSender, current: String): World? =
        Bukkit.getWorld(current)

    override fun completion(sender: CommandSender, current: String): List<String> =
        worlds

    override fun resolve(
        sender: CommandSender,
        args: ParsedArguments
    ): World? = args.flags[key]?.let(Bukkit::getWorld) ?: (sender as? Entity)?.world
}

/**
 * @see WorldArgument
 */
fun worldArgument(key: String, required: Boolean = false) =
    WorldArgument(key, required)
