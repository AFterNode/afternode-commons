package cn.afternode.commons.bukkit.kotlin

import org.bukkit.Bukkit

class TabBuilder(
    val ignoreCase: Boolean = false
) {
    private val list = ArrayList<String>()

    fun add(vararg items: String, prefix: String = "", ignoreCase: Boolean = this.ignoreCase): TabBuilder {
        items.forEach {
            if (it.startsWith(prefix, ignoreCase)) list.add(it)
        }
        return this
    }

    fun players(prefix: String = "", ignoreCase: Boolean = this.ignoreCase): TabBuilder =
        players { it.startsWith(prefix, ignoreCase) }

    fun players(filter: (String) -> Boolean): TabBuilder {
        Bukkit.getOnlinePlayers().forEach {
            if (filter(it.name)) this.list.add(it.name)
        }
        return this
    }

    fun worlds(prefix: String, ignoreCase: Boolean = this.ignoreCase): TabBuilder =
        worlds { it.startsWith(prefix, ignoreCase) }

    fun worlds(filter: (String) -> Boolean): TabBuilder {
        Bukkit.getWorlds().forEach {
            if (filter(it.name)) this.list.add(it.name)
        }
        return this
    }

    fun build() = list.toMutableList()
}

fun tabComplete(block: TabBuilder.() -> Unit): List<String> {
    val builder = TabBuilder()
    block(builder)
    return builder.build()
}
