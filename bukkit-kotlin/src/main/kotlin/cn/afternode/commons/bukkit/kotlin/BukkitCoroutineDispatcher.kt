package cn.afternode.commons.bukkit.kotlin

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
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
