package com.xiao.base.thread

import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author lix wang
 */
abstract class KtFastThreadLocal<T> {
    private var threadLocal: ThreadLocal<T>? = null
    private val index = nextIndex

    @Suppress("UNCHECKED_CAST")
    fun get() : T? {
        val thread = Thread.currentThread()
        return if (thread is KtThread) {
            if (thread.indexedVariables != null && index < thread.indexedVariables!!.size) {
                thread.indexedVariables!![index] as T?
            } else {
                null
            }
        } else {
            threadLocal?.get()
        }
    }

    fun getDefault(func: () -> T) : T {
        return get() ?: func().also { set(it) }
    }

    fun set(value: T?) {

    }

    companion object {
        private val indexGenerator = AtomicInteger(0)
        val nextIndex = indexGenerator.getAndIncrement()
    }
}