package cn.afternode.commons.bukkit.kotlin.command

import cn.afternode.commons.bukkit.kotlin.commandSuggestion
import cn.afternode.commons.bukkit.kotlin.message
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission
import java.awt.Color
import kotlin.reflect.KProperty

abstract class CompositeCommand(
    name: String,
    val namespace: String,
    val rootPermission: Permission? = null,
): Command(name) {
    internal val resolution = hashMapOf<String, SubCommand>()

    protected var helpMessagePrefix: Component? = null
    protected var helpMessageHeader: Component = Component.empty()

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
            // build helps
            p0.sendMessage(message(linePrefix = this.helpMessagePrefix ?: Component.empty()) {
                append(helpMessageHeader)

                for (entry in resolution) {
                    val help = entry.value.helpProvider(p0) ?: continue
                    line().text(entry.key).text(" - ", Color.GRAY).append(help)
                }
            })
        } else {
            val command = this.resolution[sub]!!

            if (command.permission != null && !p0.hasPermission(command.permission!!)) {
                p0.sendMessage(command.permissionMessage)
                return true
            }

            command.execute(p0, p2.sliceArray(1..<p2.size))
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
                    this.addAll(comp)
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
    private val keyedFlags = hashMapOf<String, FlagResolver<*>>()

    var permission: Permission? = permission1
        set(value) {
            checkFrozen("set permission")
            field = value
        }

    var helpProvider: (CommandSender) -> Component? = { null }
        set(value) {
            checkFrozen("set help provider")
            field = value
        }
    var permissionMessage: Component = Component.text("Sorry, but you have no permission to do that")
        set(value) {
            checkFrozen("set permission message")
            field = value
        }

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
        this.keyedFlags[flag.key] = flag
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
            val r = parsed.flags[resolver.key]?.let { resolver.resolve(sender, it) }
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

    fun completion(sender: CommandSender, args: Array<out String>): List<String> {
        val last = args.last()
        val parsed = ParsedArguments(*args)
        val results = arrayListOf<String>()

        for ((index, string) in parsed.args.withIndex()) {  // resolve arguments
            val resolver = this.arguments.getOrNull(index) ?: break
            results.addAll(resolver.completion(sender, string))
        }



        // incomplete flag
        if (parsed.lastFlag == null) {
            // resolve flags
            for (resolver in this.flags) {
                val comp = if (resolver.key.length == 1)
                    "-${resolver.key}"
                else
                    "--${resolver.key}"
                if (comp.startsWith(last))
                    results += comp
            }
        } else {
            val last = this.keyedFlags[parsed.lastFlag!!.lowercase()]
            if (last != null) {
                val comp = if (last.key.length == 1)
                    "-${last.key}"
                else
                    "--${last.key}"
                results += last.completion(sender, parsed.flags[parsed.lastFlag] ?: "")
                    .map { "$comp=$it" }
            }
        }

        return results
    }

    private fun failBadArgs(sender: CommandSender) {
        this.badArgsHandler(sender)
    }

    private fun failInternalError(sender: CommandSender, error: Throwable) {
        this.internalErrorHandler(sender, error)
    }
}
