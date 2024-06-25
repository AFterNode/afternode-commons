package cn.afternode.commons.advntr;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.TextColor;

import java.awt.*;

public class AdvntrMessageBuilder {
    private ComponentLike linePrefix = Component.text();
    private Audience audience;

    private final TextComponent.Builder component = Component.text();

    /**
     * Append raw text
     * @param text Raw text
     * @return This builder
     */
    public AdvntrMessageBuilder text(String text) {
        this.component.append(Component.text(text));
        return this;
    }

    /**
     * Append colored raw text
     * @param text Raw text
     * @param color AWT color
     * @return This builder
     * @see java.awt.Color
     */
    public AdvntrMessageBuilder text(String text, Color color) {
        this.component.append(Component.text(text).color(TextColor.color(color.getRGB())));
        return this;
    }

    /**
     * Append HoverEvent
     * @param source Event source
     * @return This builder
     * @see net.kyori.adventure.text.event.HoverEvent
     */
    public AdvntrMessageBuilder hover(HoverEventSource<?> source) {
        this.component.hoverEvent(source);
        return this;
    }

    /**
     * Append click event
     * @param event Event
     * @return This builder
     * @see ClickEvent
     */
    public AdvntrMessageBuilder click(ClickEvent event) {
        this.component.clickEvent(event);
        return this;
    }

    /**
     * Append adventure component
     * @param componentLike Component
     * @return This builder
     * @see Component
     */
    public AdvntrMessageBuilder append(ComponentLike componentLike) {
        this.component.append(componentLike);
        return this;
    }

    /**
     * Append new line with prefix
     * @return This builder
     * @see #linePrefix
     * @see #linePrefix(ComponentLike)
     */
    public AdvntrMessageBuilder line() {
        this.component.appendNewline();
        this.component.append(linePrefix);
        return this;
    }

    /**
     * Append empty line
     * @return This builder
     */
    public AdvntrMessageBuilder emptyLine() {
        this.component.appendNewline();
        return this;
    }

    /**
     * Convert this builder to an Adventure component
     * @return Adventure component
     */
    public TextComponent build() {
        return this.component.build();
    }

    /**
     * Convert this builder to Adventure component and send it to sender of this builder
     * <br>
     * A sender must be passed to this builder
     * @see #audience(Audience)
     * @see #audience
     * @see #build()
     */
    public void send() {
        if (this.audience == null)
            throw new NullPointerException("No audience passed to this builder");
        this.audience.sendMessage(this.build());
    }

    /**
     * Convert this builder to Adventure component and send it to sender
     * @param audience Adventure audience
     * @see Audience#sendMessage(Component)
     * @see #send()
     * @see #build()
     */
    public void send(Audience audience) {
        audience.sendMessage(this.build());
    }

    public ComponentLike getLinePrefix() {
        return linePrefix;
    }

    public AdvntrMessageBuilder linePrefix(ComponentLike prefix) {
        this.linePrefix = prefix;
        return this;
    }

    public Audience getAudience() {
        return audience;
    }

    public AdvntrMessageBuilder audience(Audience audience) {
        this.audience = audience;
        return this;
    }
}
