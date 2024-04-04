package cn.afternode.commons.bukkit.kotlin

import org.bukkit.Bukkit

class TabBuilder {
    private val list = ArrayList<String>()

    fun add(vararg items: String, prefix: String = "", ignoreCase: Boolean=false): TabBuilder {
        items.forEach {
            if (it.startsWith(prefix, ignoreCase)) list.add(it)
        }
        return this
    }

    fun players(prefix: String = "", ignoreCase: Boolean=false): TabBuilder {
        Bukkit.getOnlinePlayers().forEach {
            if (it.name.startsWith(prefix, ignoreCase)) list.add(it.name)
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
