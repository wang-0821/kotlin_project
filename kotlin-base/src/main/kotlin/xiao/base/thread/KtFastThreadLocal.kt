package xiao.base.thread

import io.netty.util.concurrent.FastThreadLocal
import io.netty.util.concurrent.FastThreadLocalThread
import java.util.concurrent.atomic.AtomicInteger

/**
 * Suggest not use [KtFastThreadLocal] as local variables.
 *
 * @author lix wang
 */
class KtFastThreadLocal<T> {
    private var threadLocal: ThreadLocal<T>? = null
    private var fastThreadLocal: FastThreadLocal<T>? = null
    private val index = nextIndex()

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
            is FastThreadLocalThread -> fastThreadLocal?.get()
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
            is FastThreadLocalThread -> {
                if (fastThreadLocal == null) {
                    synchronized(this) {
                        if (fastThreadLocal == null) {
                            fastThreadLocal = FastThreadLocal<T>()
                        }
                    }
                }
                fastThreadLocal!!.set(value)
            }
            else -> {
                if (threadLocal == null) {
                    synchronized(this) {
                        if (threadLocal == null) {
                            threadLocal = ThreadLocal<T>()
                        }
                    }
                }
                threadLocal!!.set(value)
            }
        }
    }

    fun reset() {
        get()?.let {
            if (it is AutoCloseable) {
                it.close()
            }
            set(null)
        }
    }

    private fun expandIndexVariables(src: Array<Any?>, size: Int): Array<Any?> {
        val dest = arrayOfNulls<Any?>(size)
        System.arraycopy(src, 0, dest, 0, src.size)
        return dest
    }

    private fun tableSizeFor(index: Int): Int {
        var size = index
        size = size or (size ushr 1)
        size = size or (size ushr 2)
        size = size or (size ushr 4)
        size = size or (size ushr 8)
        size = size or (size ushr 16)

        size = when {
            size < 0 -> {
                1
            }
            size >= Int.MAX_VALUE -> {
                Int.MAX_VALUE
            }
            else -> {
                size + 1
            }
        }
        return size.coerceAtLeast(16)
    }

    companion object {
        private val indexGenerator = AtomicInteger(0)
        private fun nextIndex() = indexGenerator.getAndIncrement()
    }
}