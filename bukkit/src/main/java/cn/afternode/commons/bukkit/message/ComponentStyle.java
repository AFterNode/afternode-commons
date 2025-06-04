package cn.afternode.commons.bukkit.message;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.awt.*;

public class ComponentStyle {
    private TextColor color = TextColor.color(0xFFFFFF);
    private boolean italic = false;
    private boolean bold = false;
    private boolean underline = false;
    private boolean strike = false;
    private boolean obfuscated = false;

    /**
     * Get current text color
     * @return Result
     */
    public TextColor color() {
        return color;
    }

    /**
     * Set current text color
     * @param color value
     * @return this builder
     * @see Style#color(TextColor) 
     */
    public ComponentStyle color(TextColor color) {
        this.color = color;
        return this;
    }

    /**
     * Set current text color from an AWT color
     * @param color value
     * @return this builder
     * @see TextColor#color(int)
     * @see Style#color(TextColor) 
     */
    public ComponentStyle color(Color color) {
        this.color = TextColor.color(color.getRGB());
        return this;
    }

    /**
     * Get italic state
     * @return state
     * @see TextDecoration#ITALIC
     */
    public boolean italic() {
        return italic;
    }

    /**
     * Set italic state
     * @param italic value
     * @return this builder
     * @see TextDecoration#ITALIC
     */
    public ComponentStyle italic(boolean italic) {
        this.italic = italic;
        return this;
    }

    /**
     * Get bold state
     * @return state
     * @see TextDecoration#BOLD
     */
    public boolean bold() {
        return bold;
    }

    /**
     * Set bold state
     * @param bold value
     * @return this builder
     * @see TextDecoration#BOLD
     */
    public ComponentStyle bold(boolean bold) {
        this.bold = bold;
        return this;
    }

    /**
     * Get underlined state
     * @return state
     * @see TextDecoration#UNDERLINED
     */
    public boolean underline() {
        return underline;
    }

    /**
     * Set underlined state
     * @param underline value
     * @return this builder
     * @see TextDecoration#UNDERLINED
     */
    public ComponentStyle underline(boolean underline) {
        this.underline = underline;
        return this;
    }

    /**
     * Get strikethrough state
     * @return state
     * @see TextDecoration#STRIKETHROUGH
     */
    public boolean strike() {
        return strike;
    }

    /**
     * Set strikethrough state
     * @param strike state
     * @return this builder
     * @see TextDecoration#STRIKETHROUGH
     */
    public ComponentStyle strike(boolean strike) {
        this.strike = strike;
        return this;
    }

    /**
     * Get obfuscated state
     * @return state
     * @see TextDecoration#OBFUSCATED
     */
    public boolean obfuscated() {
        return obfuscated;
    }

    /**
     * Set obfuscated state
     * @param obfuscated value
     * @return this builder
     * @see TextDecoration#OBFUSCATED
     */
    public ComponentStyle obfuscated(boolean obfuscated) {
        this.obfuscated = obfuscated;
        return this;
    }

    /**
     * Build to {@link Style}
     * @return result
     * @see Style
     */
    public Style build() {
        return this.makeBuilder().build();
    }

    /**
     * Build to {@link Style} without color
     * @return result
     */
    public Style buildNoColor() {
        Style.Builder builder = Style.style();
        builder.decoration(TextDecoration.ITALIC, italic);
        builder.decoration(TextDecoration.BOLD, bold);
        builder.decoration(TextDecoration.UNDERLINED, underline);
        builder.decoration(TextDecoration.STRIKETHROUGH, strike);
        builder.decoration(TextDecoration.OBFUSCATED, obfuscated);
        return builder.build();
    }

    Style.Builder makeBuilder() {
        Style.Builder builder = Style.style();
        builder.color(color);
        builder.decoration(TextDecoration.ITALIC, italic);
        builder.decoration(TextDecoration.BOLD, bold);
        builder.decoration(TextDecoration.UNDERLINED, underline);
        builder.decoration(TextDecoration.STRIKETHROUGH, strike);
        builder.decoration(TextDecoration.OBFUSCATED, obfuscated);
        return builder;
    }
}
