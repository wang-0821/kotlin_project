package com.xiao.base.lock

import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * @author lix wang
 */
class SpinLock {
    private val value = AtomicBoolean(false)

    fun <T> use(block: () -> T): T {
        lock()
        try {
            return block()
        } finally {
            unlock()
        }
    }

    fun unlock() {
        value.compareAndSet(true, false)
    }

    fun lock() {
        @Suppress("ControlFlowWithEmptyBody")
        while (!value.compareAndSet(false, true)) {
        }
    }
}