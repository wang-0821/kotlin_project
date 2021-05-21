package com.xiao.base.thread

import java.util.concurrent.atomic.AtomicInteger

/**
 * Suggest not use [KtFastThreadLocal] as local variables.
 *
 * @author lix wang
 */
class KtFastThreadLocal<T> {
    private var threadLocal: ThreadLocal<T>? = null
    private val index = nextIndex

    @Suppress("UNCHECKED_CAST")
    fun get(): T? {
        return when (val thread = Thread.currentThread()) {
            is ThreadLocalValueProvider -> {
                if (thread.indexedVariables != null && index < thread.indexedVariables!!.size) {
                    thread.indexedVariables!![index] as T?
                } else {
                    null
                }
            }
            else -> {
                threadLocal?.get()
            }
        }
    }

    fun fetch(func: () -> T): T {
        return get() ?: func().also { set(it) }
    }

    fun set(value: T?) {
        when (val thread = Thread.currentThread()) {
            is ThreadLocalValueProvider -> {
                if (thread.indexedVariables == null) {
                    // initial
                    thread.indexedVariables = arrayOfNulls(tableSizeFor(index))
                    thread.indexedVariables!![index] = value
                } else {
                    // out of range, do expand.
                    if (index >= thread.indexedVariables!!.size) {
                        thread.indexedVariables = expandIndexVariables(thread.indexedVariables!!, tableSizeFor(index))
                    }
                    thread.indexedVariables!![index] = value
                }
            }
            else -> {
                if (threadLocal == null) {
                    threadLocal = ThreadLocal<T>()
                }
                threadLocal!!.set(value)
            }
        }
    }

    private fun expandIndexVariables(src: Array<Any?>, size: Int): Array<Any?> {
        val dest = arrayOfNulls<Any?>(size)
        System.arraycopy(src, 0, dest, 0, src.size)
        return dest
    }

    private fun tableSizeFor(index: Int): Int {
        var value = index
        value = value or value ushr 1
        value = value or value ushr 2
        value = value or value ushr 4
        value = value or value ushr 8
        value = value or value ushr 16

        return when {
            value < 0 -> {
                1
            }
            value >= Int.MAX_VALUE -> {
                Int.MAX_VALUE
            }
            else -> {
                value + 1
            }
        }
    }

    companion object {
        private val indexGenerator = AtomicInteger(0)
        val nextIndex = indexGenerator.getAndIncrement()
    }
}