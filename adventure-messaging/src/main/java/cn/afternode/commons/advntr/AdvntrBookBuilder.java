package cn.afternode.commons.advntr;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.inventory.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdvntrBookBuilder {
    private AdvntrMessageBuilder title = new AdvntrMessageBuilder();
    private AdvntrMessageBuilder author = new AdvntrMessageBuilder();
    private final List<AdvntrMessageBuilder> pages = new ArrayList<>();

    public AdvntrBookBuilder newTitle(AdvntrMessageBuilder builder) {
        this.title = builder;
        return this;
    }

    public AdvntrBookBuilder newAuthor(AdvntrMessageBuilder builder) {
        this.author = builder;
        return this;
    }

    public AdvntrBookBuilder page(AdvntrMessageBuilder builder) {
        this.pages.add(builder);
        return this;
    }

    public int size() {
        return this.pages.size();
    }

    public Book build() {
        return Book.book(title.build(), author.build(), pages.stream().map(AdvntrMessageBuilder::build).collect(Collectors.toSet()));
    }

    public void open(Audience audience) {
        audience.openBook(this.build());
    }
}
