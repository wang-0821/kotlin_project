package com.xiao.base.executor

import java.util.concurrent.Callable

/**
 *
 * @author lix wang
 */
class SimpleQueueItem<T>(name: String, private val callable: Callable<T>): QueueItem<T>(name) {
    override fun call(): T {
        return callable.call()
    }
}