package cn.afternode.commons.bukkit.kotlin

import cn.afternode.commons.localizations.ILocalizations
import net.kyori.adventure.inventory.Book

class BookBuilder(
    private val locale: ILocalizations? = null
) {
    var title = MessageBuilder(locale)
        private set
    var author = MessageBuilder(locale)
        private set
    private val pages = mutableListOf<MessageBuilder>()

    /**
     * Create new title
     */
    fun newTitle(block: MessageBuilder.() -> Unit): BookBuilder {
        title = MessageBuilder(locale)
        block(title)
        return this
    }

    /**
     * Create new author
     */
    fun newAuthor(block: MessageBuilder.() -> Unit): BookBuilder {
        author = MessageBuilder(locale)
        block(author)
        return this
    }

    /**
     * Edit current title
     */
    fun title(block: MessageBuilder.() -> Unit): BookBuilder {
        block(title)
        return this
    }

    /**
     * Edit current author
     */
    fun author(block: MessageBuilder.() -> Unit): BookBuilder {
        block(author)
        return this
    }

    /**
     * Create new page
     */
    fun page(block: MessageBuilder.() -> Unit): BookBuilder {
        val page = MessageBuilder()
        block(page)
        this.pages.add(page)
        return this
    }

    /**
     * Edit existing page
     */
    fun editPage(index: Int, block: MessageBuilder.() -> Unit): BookBuilder {
        block(pages[index])
        return this
    }

    /**
     * Get book size
     */
    fun size() = pages.size

    /**
     * Build adventure [book](https://docs.advntr.dev/book.html)
     * @see Book
     */
    fun build() = Book.book(title.build(), author.build(), pages.map { it.build() })
}

fun book(locale: ILocalizations? = null, block: BookBuilder.() -> Unit): Book {
    val builder = BookBuilder(locale)
    block(builder)
    return builder.build()
}
