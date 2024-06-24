package cn.afternode.commons.bukkit.kotlin

import cn.afternode.commons.bukkit.message.MessageBuilder
import cn.afternode.commons.localizations.ILocalizations
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.command.CommandSender
import java.net.URL

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
    fun showText(locale: ILocalizations? = null, linePrefix: ComponentLike? = null, builder: MessageBuilder.() -> Unit) {
        val mb = MessageBuilder(locale, linePrefix, null)
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
