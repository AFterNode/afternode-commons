package cn.afternode.commons.bukkit.kotlin.command

import org.bukkit.command.CommandSender

/**
 * Base interface of argument resolvers
 * @see cn.afternode.commons.bukkit.kotlin.command.argument
 */
interface ArgumentResolver<T : Any> {
    /**
     * Key of this argument
     */
    val key: String

    /**
     * Parse string to argument
     * @param sender Executor of current command
     * @param current Current argument
     */
    fun resolve(sender: CommandSender, current: String): T?

    /**
     * Create completions
     * @param sender Executor of current sender
     * @param current Current argument
     */
    fun completion(sender: CommandSender, current: String): List<String> =
        emptyList()
}

/**
 * Base interface of flag resolvers
 * @see cn.afternode.commons.bukkit.kotlin.command.argument
 */
interface FlagResolver<T : Any> {
    /**
     * Key of this flag
     */
    val key: String

    /**
     * Treat this flag as required
     */
    val required: Boolean

    /**
     * Resolve arguments to flags
     * @param sender Executor of current command
     * @param args Pre-parsed arguments of current command
     */
    fun resolve(sender: CommandSender, args: ParsedArguments): T?
}
