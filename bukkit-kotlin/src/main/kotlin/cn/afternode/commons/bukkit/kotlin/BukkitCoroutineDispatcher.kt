package cn.afternode.commons.bukkit.kotlin

import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import kotlin.coroutines.CoroutineContext

/**
 * Kotlin coroutine dispatcher for Bukkit scheduler
 * Requires org.jetbrains.kotlinx:kotlinx-coroutines-core
 */
class BukkitCoroutineDispatcher(private val plugin: Plugin): CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        Bukkit.getScheduler().runTask(plugin, block)
    }
}

fun BukkitScheduler.asCoroutineDispatcher(plugin: Plugin): CoroutineDispatcher = BukkitCoroutineDispatcher(plugin)
fun BukkitScheduler.coroutineLaunch(plugin: Plugin, block: suspend CoroutineScope.() -> Unit): Job = CoroutineScope(asCoroutineDispatcher(plugin)).launch(block = block)
