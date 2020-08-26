package com.xiao.base.executor

/**
 *
 * @author lix wang
 */
class SimpleQueueItem(name: String, runnable: Runnable) : QueueItem(name, runnable) {
    override fun run() {

    }
}