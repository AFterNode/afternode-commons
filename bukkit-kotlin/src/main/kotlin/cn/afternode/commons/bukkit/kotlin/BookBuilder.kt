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

    fun newTitle(block: MessageBuilder.() -> Unit): BookBuilder {
        title = MessageBuilder(locale)
        block(title)
        return this
    }

    fun newAuthor(block: MessageBuilder.() -> Unit): BookBuilder {
        author = MessageBuilder(locale)
        block(author)
        return this
    }

    fun title(block: MessageBuilder.() -> Unit): BookBuilder {
        block(title)
        return this
    }

    fun author(block: MessageBuilder.() -> Unit): BookBuilder {
        block(author)
        return this
    }

    fun page(block: MessageBuilder.() -> Unit): BookBuilder {
        val page = MessageBuilder()
        block(page)
        this.pages.add(page)
        return this
    }

    fun editPage(index: Int, block: MessageBuilder.() -> Unit): BookBuilder {
        block(pages[index])
        return this
    }

    fun size() = pages.size

    fun build() = Book.book(title.build(), author.build(), pages.map { it.build() })
}

fun book(locale: ILocalizations? = null, block: BookBuilder.() -> Unit): Book {
    val builder = BookBuilder(locale)
    block(builder)
    return builder.build()
}
