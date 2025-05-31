package cn.afternode.commons.bukkit.kotlin.command

import cn.afternode.commons.bukkit.kotlin.commandSuggestion
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission
import kotlin.reflect.KProperty

abstract class CompositeCommand(
    name: String,
    val namespace: String,
    val rootPermission: Permission? = null,
): Command(name) {
    internal val resolution = hashMapOf<String, SubCommand>()

    fun doRegister() = this.doRegister(Bukkit.getCommandMap())

    fun doRegister(map: CommandMap) {
        map.register(namespace, this)
    }

    protected fun sub(
        name: String? = null,
        permission: Permission? = null,
        builder: SubCommand.() -> Unit
    ) =
        SubCommandProvider(name=name, permission=permission, builder=builder)

    protected fun add(command: SubCommand) {
        this.resolution[command.name.lowercase()] = command
    }

    override fun execute(p0: CommandSender, p1: String, p2: Array<String>): Boolean {
        val sub = p2.firstOrNull()?.lowercase()
        if (sub == null || sub !in resolution) {
            // TODO help
        } else {
            this.resolution[sub]!!.execute(p0, p2.sliceArray(1..<p2.size))
        }

        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): List<String> =
        commandSuggestion {
            if (args.size == 1) {
                this.add(args[0], *resolution.keys.toTypedArray())
            } else {
                val slice = args.sliceArray(1..<args.size)
                val comp = resolution[args[0].lowercase()]?.completion(sender, slice)
                if (comp != null)
                    this.add(slice.lastOrNull() ?: "", *comp)
            }
        }
}

class SubCommandProvider(val name: String? = null, val permission: Permission? = null, val builder: SubCommand.() -> Unit) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): SubCommand {
        val name = this.name ?: property.name
        val cmd = SubCommand(name.lowercase(), permission)
        cmd.builder()
        cmd.freeze()
        return cmd
    }
}

class SubCommand(
    val name: String,
    permission1: Permission?
) {
    private var frozen = false

    private val arguments = arrayListOf<ArgumentResolver<*>>()
    private val flags = arrayListOf<FlagResolver<*>>()

    var permission: Permission? = permission1
        set(value) {
            checkFrozen("set permission")
            field = value
        }

    var helpProvider: (CommandSender) -> Component? = { null }
    var executes: CommandContext.() -> Unit = {}
        set(value) {
            checkFrozen("set executor")
            field = value
        }
    var badArgsHandler: (CommandSender) -> Unit = {}
        set(value) {
            checkFrozen("set bad arguments handler")
            field = value
        }
    var internalErrorHandler: (CommandSender, Throwable) -> Unit = { _, _ -> }
        set(value) {
            checkFrozen("set internal error handler")
            field = value
        }

    private fun checkFrozen(operation: String) {
        if (frozen)
            throw IllegalStateException("Cannot $operation after command frozen")
    }

    internal fun freeze() {
        checkFrozen("freeze")
        this.frozen = true
    }

    fun argument(argument: ArgumentResolver<*>) {
        this.checkFrozen("add argument")
        this.arguments += argument
    }

    fun flag(flag: FlagResolver<*>) {
        this.checkFrozen("add flag")
        this.flags += flag
    }

    fun execute(sender: CommandSender, args: Array<out String>) {
        val parsed = ParsedArguments(*args)
        if (parsed.args.size < this.arguments.size) // mismatched arguments
            return this.failBadArgs(sender)

        val resolved = hashMapOf<String, Any>()
        for ((idx, resolver) in this.arguments.withIndex()) {
            try {
                val result = resolver.resolve(
                    sender, parsed.args.getOrNull(idx)
                        ?: return failBadArgs(sender)
                )
                if (result == null)
                    return this.failBadArgs(sender)

                resolved[resolver.key] = result
            } catch (t: Throwable) {    // missing arguments
                return this.failInternalError(sender, t)
            }
        }
        for (resolver in this.flags) {
            val r = resolver.resolve(sender, parsed)
            try {
                if (r == null) {
                    if (resolver.required)    // missing parameters
                        return this.failBadArgs(sender)
                } else resolved[resolver.key] = r
            } catch (_: Throwable) {
                return this.failBadArgs(sender)
            }
        }

        try {
            this.executes.invoke(CommandContext(sender, resolved.toMap()))
        } catch (t: Throwable) {
            this.failInternalError(sender, t)
        }
    }

    fun completion(sender: CommandSender, args: Array<out String>): Array<String> {
        val parsed = ParsedArguments(*args)
        val results = arrayListOf<String>()

        for ((index, string) in parsed.args.withIndex()) {  // resolve arguments
            val resolver = this.arguments.getOrNull(index) ?: break
            results.addAll(resolver.completion(sender, string))
        }

        // resolve flags
        for (resolver in this.flags) {
            results += if (resolver.key.length == 1)
                "-${resolver.key}"
            else
                "--${resolver.key}"
        }

        return results.toTypedArray()
    }

    private fun failBadArgs(sender: CommandSender) {
        this.badArgsHandler(sender)
    }

    private fun failInternalError(sender: CommandSender, error: Throwable) {
        this.internalErrorHandler(sender, error)
    }
}
