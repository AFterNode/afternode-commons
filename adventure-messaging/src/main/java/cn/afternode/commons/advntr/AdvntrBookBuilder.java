package cn.afternode.commons.advntr;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.inventory.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adventure-API implementation for book builder
 */
public class AdvntrBookBuilder {
    private AdvntrMessageBuilder title = new AdvntrMessageBuilder();
    private AdvntrMessageBuilder author = new AdvntrMessageBuilder();
    private final List<AdvntrMessageBuilder> pages = new ArrayList<>();

    /**
     * Set title
     * @param builder Adventure message builder
     * @return This builder
     */
    public AdvntrBookBuilder newTitle(AdvntrMessageBuilder builder) {
        this.title = builder;
        return this;
    }

    /**
     * Set author
     * @param builder Adventure message builder
     * @return This builder
     */
    public AdvntrBookBuilder newAuthor(AdvntrMessageBuilder builder) {
        this.author = builder;
        return this;
    }

    /**
     * Append page
     * @param builder Adventure message builder
     * @return This builder
     */
    public AdvntrBookBuilder page(AdvntrMessageBuilder builder) {
        this.pages.add(builder);
        return this;
    }

    /**
     * Get page size
     * @return Page size
     */
    public int size() {
        return this.pages.size();
    }

    /**
     * Build
     * @return Adventure book object
     */
    public Book build() {
        return Book.book(title.build(), author.build(), pages.stream().map(AdvntrMessageBuilder::build).collect(Collectors.toSet()));
    }

    /**
     * Open for Audience
     * @param audience Adventure audience
     */
    public void open(Audience audience) {
        audience.openBook(this.build());
    }
}
