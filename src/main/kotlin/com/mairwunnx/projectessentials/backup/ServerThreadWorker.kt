package com.mairwunnx.projectessentials.backup

import java.util.concurrent.ConcurrentHashMap

object ServerThreadWorker {
    private val queueMap = ConcurrentHashMap<Runnable, () -> Any>()
    private var ticks = 0

    fun execute(task: Runnable, callback: () -> Any) =
        synchronized(queueMap) { queueMap[task] = { callback() } }

    fun handle() {
        ticks += 1
        if (ticks == 20) {
            ticks = 0
            if (queueMap.keys.isNotEmpty() && queueMap.values.isNotEmpty()) {
                val task = queueMap.keys.last()
                val callback = queueMap.values.last()
                task.run().also { callback() }.also {
                    queueMap.keys.remove(task).also { queueMap.values.remove(callback) }
                }
            }
        }
    }

    fun refresh() = queueMap.clear().also { ticks = 0 }
}
