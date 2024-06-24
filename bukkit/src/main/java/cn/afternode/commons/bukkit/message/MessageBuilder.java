package cn.afternode.commons.bukkit.message;

import cn.afternode.commons.localizations.ILocalizations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MessageBuilder {
    private final ILocalizations localizations;
    private ComponentLike linePrefix = Component.text();
    private final CommandSender sender;

    private final TextComponent.Builder component = Component.text();

    public MessageBuilder(
            @Nullable ILocalizations locale,
            @Nullable ComponentLike linePrefix,
            @Nullable CommandSender sender
            ) {
        this.localizations = locale;
        if (linePrefix != null) this.linePrefix = linePrefix;
        this.sender = sender;

        component.append(this.linePrefix);
    }

    public MessageBuilder localize(String key, Map<String, Object> placeholders) {
        if (localizations == null)
            throw new NullPointerException("No localizations passed to this builder");
        component.append(Component.text(this.localizations.get(key, placeholders)));
        return this;
    }

    public MessageBuilder text(String text) {
        this.component.append(Component.text(text));
        return this;
    }

    public MessageBuilder text(String text, Color color) {
        this.component.append(Component.text(text).color(TextColor.color(color.asRGB())));
        return this;
    }

    public MessageBuilder mini(String mini) {
        this.component.append(MiniMessage.miniMessage().deserialize(mini));
        return this;
    }

    public MessageBuilder hover(HoverEventSource<?> source) {
        this.component.hoverEvent(source);
        return this;
    }

    public MessageBuilder click(ClickEvent event) {
        this.component.clickEvent(event);
        return this;
    }

    public MessageBuilder append(ComponentLike componentLike) {
        this.component.append(componentLike);
        return this;
    }

    public MessageBuilder line() {
        this.component.appendNewline();
        this.component.append(linePrefix);
        return this;
    }

    public MessageBuilder emptyLine() {
        this.component.appendNewline();
        return this;
    }

    public MessageBuilder permission(String permission, ComponentLike componentLike) {
        if (sender.hasPermission(permission))
            this.component.append(componentLike);
        return this;
    }

    public MessageBuilder permission(Permission permission, ComponentLike component) {
        if (sender.hasPermission(permission))
            this.component.append(component);
        return this;
    }

    public TextComponent build() {
        return this.component.build();
    }

    public void send() {
        if (this.sender == null)
            throw new NullPointerException("No sender passed to this builder");
        this.sender.sendMessage(this.build());
    }

    public void send(CommandSender sender) {
        sender.sendMessage(this.build());
    }

    public ILocalizations getLocalizations() {
        return localizations;
    }

    public ComponentLike getLinePrefix() {
        return linePrefix;
    }

    public CommandSender getSender() {
        return sender;
    }
}
