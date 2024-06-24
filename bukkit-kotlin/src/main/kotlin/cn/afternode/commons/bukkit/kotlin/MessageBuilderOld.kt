package cn.afternode.commons.bukkit.kotlin

import cn.afternode.commons.localizations.ILocalizations
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.event.HoverEventSource
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission
import java.awt.Color
import java.net.URL

/**
 * MessageBuilder for Adventure component
 *
 * Requires [adventure-api](https://docs.advntr.dev/getting-started.html)
 *
 * @param locale Localizations for localize()
 */
@Deprecated(message = "Moved to Java")
class MessageBuilder(
    private val locale: ILocalizations? = null,
    private val linePrefix: ComponentLike = Component.empty(),
    private val sender: CommandSender? = null
) {
    private val component = Component.text()

    init {
        append(linePrefix)
    }

    /**
     * Append localized string
     *
     * Requires locale object provided
     *
     * @see ILocalizations
     */
    fun localize(key: String, vararg p: Pair<String, Any>): MessageBuilder {
        component.append(Component.text(locale?.get(key, mapOf(*p)) ?: throw NullPointerException("No localizations provided")))
        return this
    }

    /**
     * Append raw text
     */
    fun text(text: String, color: Color? = null): MessageBuilder {
        component.append(
            if (color != null) Component.text(text).color(TextColor.color(color.rgb)) else Component.text(text)
        )
        return this
    }

    /**
     * Append [MiniMessage](https://docs.advntr.dev/minimessage/index.html)
     *
     * Requires [adventure-text-minimessage](https://docs.advntr.dev/minimessage/api.html)
     *
     * @see MiniMessage
     */
    fun mini(mini: String): MessageBuilder {
        component.append(MiniMessage.miniMessage().deserialize(mini))
        return this
    }

    /**
     * Append hover event from HoverEventSource
     * @see HoverEventSource
     * @see HoverEvent
     */
    fun hover(src: HoverEventSource<*>): MessageBuilder {
        component.hoverEvent(src)
        return this
    }

    /**
     * Append hover event from HoverBuilder
     * @see HoverBuilder
     */
    fun hover(builder: HoverBuilder.() -> Unit): MessageBuilder {
        val b = HoverBuilder()
        builder.invoke(b)
        b.build()?.let { hover(it) }
        return this
    }

    /**
     * Append ClickEvent
     * @see ClickEvent
     */
    fun click(event: ClickEvent): MessageBuilder {
        component.clickEvent(event)
        return this
    }

    /**
     * Append ClickEvent with ClickBuilder
     * @see ClickBuilder
     */
    fun click(builder: ClickBuilder.() -> Unit): MessageBuilder {
        val cb = ClickBuilder()
        builder.invoke(cb)
        cb.build()?.let { click(it) }
        return this
    }

    /**
     * Append MessageBuilder
     */
    fun append(builder: MessageBuilder): MessageBuilder {
        component.append(builder.component)
        return this
    }

    fun append(component: ComponentLike): MessageBuilder {
        this.component.append(component)
        return this
    }

    /**
     * Append sub MessageBuilder
     */
    fun sub(builder: MessageBuilder.() -> Unit): MessageBuilder {
        val b = MessageBuilder(linePrefix = linePrefix)
        builder.invoke(b)
        return append(b)
    }

    /**
     * Append new line
     */
    fun line(): MessageBuilder {
        component.appendNewline()
        component.append(linePrefix)
        return this
    }

    /**
     * Append empty line
     */
    fun emptyLine(): MessageBuilder {
        component.appendNewline()
        return this
    }

    /**
     * Append component if the sender has specified permission
     * @param sender Message sender
     * @param permission Target permission
     * @param block Message builder
     */
    fun permission(sender: CommandSender, permission: String, block: MessageBuilder.() -> Unit): MessageBuilder {
        if (sender.hasPermission(permission)) this.sub(block)
        return this
    }

    /**
     * Append component if sender of this builder has specified permission
     * @param permission Target permission
     * @param block Message builder
     */
    fun permission(permission: String, block: MessageBuilder.() -> Unit): MessageBuilder = this.permission(this.sender ?: throw NullPointerException("No CommandSender passed to this builder"), permission, block)

    /**
     * Append component if the sender has specified permission
     * @param sender Message sender
     * @param permission Target permission
     * @param component Component to append
     */
    fun permission(sender: CommandSender, permission: String, component: ComponentLike): MessageBuilder {
        if (sender.hasPermission(permission)) this.append(component)
        return this
    }

    /**
     * Append component if sender of this builder has specified permission
     * @param permission Target permission
     * @param component Component to append
     */
    fun permission(permission: String, component: ComponentLike): MessageBuilder = this.permission(this.sender ?: throw NullPointerException("No CommandSender passed to this builder"), permission, component)

    /**
     * Append component if the sender has specified permission
     * @param sender Message sender
     * @param permission Target permission
     * @param block Message builder
     */
    fun permission(sender: CommandSender, permission: Permission, block: MessageBuilder.() -> Unit): MessageBuilder {
        if (sender.hasPermission(permission)) this.sub(block)
        return this
    }

    /**
     * Append component if sender of this builder has specified permission
     * @param permission Target permission
     * @param block Message builder
     */
    fun permission(permission: Permission, block: MessageBuilder.() -> Unit): MessageBuilder = this.permission(this.sender ?: throw NullPointerException("No CommandSender passed to this builder"), permission, block)

    /**
     * Append component if the sender has specified permission
     * @param sender Message sender
     * @param permission Target permission
     * @param component Component to append
     */
    fun permission(sender: CommandSender, permission: Permission, component: ComponentLike): MessageBuilder {
        if (sender.hasPermission(permission)) this.append(component)
        return this
    }

    /**
     * Append component if sender of this builder has specified permission
     * @param permission Target permission
     * @param component Component to append
     */
    fun permission(permission: Permission, component: ComponentLike): MessageBuilder = this.permission(this.sender ?: throw NullPointerException("No CommandSender passed to this builder"), permission, component)

    fun build() = component.build()
}

class HoverBuilder {
    private var event: HoverEvent<*>? = null

    /**
     * Show TextComponent
     * @see TextComponent
     */
    fun showText(component: TextComponent) {
        event = HoverEvent.showText(component)
    }

    /**
     * Show TextComponent from MessageBuilder
     * @see MessageBuilder
     */
    fun showText(builder: MessageBuilder.() -> Unit) {
        val mb = MessageBuilder()
        builder.invoke(mb)
        showText(mb.build())
    }

    fun build() = event
}

class ClickBuilder {
    private var event: ClickEvent? = null

    /**
     * Open URL
     * @see ClickEvent.openUrl
     */
    fun url(url: URL) {
        event = ClickEvent.openUrl(url)
    }

    /**
     * Open URL
     * @see ClickEvent.openUrl
     */
    fun url(url: String) {
        url(URL(url))
    }

    /**
     * Open file on client
     * @see ClickEvent.openFile
     */
    fun file(file: String) {
        event = ClickEvent.openFile(file)
    }

    /**
     * Run command
     * @see ClickEvent.runCommand
     *
     */
    fun run(command: String) {
        event = ClickEvent.runCommand(command)
    }

    /**
     * Suggest command
     * @see ClickEvent.suggestCommand
     */
    fun suggest(command: String) {
        event = ClickEvent.suggestCommand(command)
    }

    /**
     * Write text to client clipboard
     * @see ClickEvent.copyToClipboard
     */
    fun copy(text: String) {
        event = ClickEvent.copyToClipboard(text)
    }

    /**
     * Run callback
     */
    fun callback(callback: (Audience) -> Unit) {
        event = ClickEvent.callback(callback)
    }

    /**
     * Fill with custom ClickEvent
     * @see ClickEvent
     */
    fun custom(event: ClickEvent) {
        this.event = event
    }

    fun build() = event
}

/**
 * Build Adventure component with MessageBuilder
 * @see cn.afternode.commons.bukkit.kotlin.MessageBuilder
 */
fun message(locale: ILocalizations? = null, linePrefix: ComponentLike = Component.empty(), sender: CommandSender? = null, block: MessageBuilder.() -> Unit): Component {
    val mb = MessageBuilder(locale, linePrefix, sender)
    block.invoke(mb)
    return mb.build()
}

/**
 * Build Adventure component with MessageBuilder (without Localizations)
 */
fun message(linePrefix: ComponentLike = Component.empty(), sender: CommandSender? = null, block: MessageBuilder.() -> Unit) = message(locale = null, linePrefix = linePrefix, sender = sender, block = block)

/**
 * Build Adventure component with TextComponent.Builder
 * @see net.kyori.adventure.text.TextComponent.Builder
 */
fun component(block: TextComponent.Builder.() -> Unit): Component {
    val c = Component.text()
    block.invoke(c)
    return c.build()
}