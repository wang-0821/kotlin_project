package xiao.base.executor

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
class CallableFuture<T>(
    private val caller: () -> T
) : Future<T> {
    private var canceled: Boolean = false
    private var done: Boolean = false

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        canceled = true
        return true
    }

    override fun isCancelled(): Boolean {
        return canceled
    }

    override fun isDone(): Boolean {
        return done
    }

    override fun get(): T {
        try {
            return caller()
        } finally {
            done = true
        }
    }

    override fun get(timeout: Long, unit: TimeUnit): T {
        return get()
    }
}