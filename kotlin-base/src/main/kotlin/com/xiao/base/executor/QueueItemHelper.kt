package com.xiao.base.executor

import java.util.concurrent.Callable

/**
 *
 * @author lix wang
 */
object QueueItemHelper {
    @Suppress("UNCHECKED_CAST")
    fun <T : Any?> getQueueItem(name: String, callable: Callable<T>): QueueItem<T> {
        return object : QueueItem<T>(name) {
            override fun execute(): T {
                return callable.call()
            }
        }
    }

    fun getQueueItem(name: String, runnable: Runnable): QueueItem<Unit> {
        return object : QueueItem<Unit>(name) {
            override fun execute() {
                runnable.run()
            }
        }
    }
}