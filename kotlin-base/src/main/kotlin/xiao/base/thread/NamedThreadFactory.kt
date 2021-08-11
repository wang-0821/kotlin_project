package xiao.base.thread

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

/**
 * 实际项目中，如果JVM中已经不存在其他用户线程，用户自己创建的线程是否还有必要继续执行？
 * 这里使用守护线程，避免死循环导致JVM无法退出。
 *
 * @author lix wang
 */
class NamedThreadFactory @JvmOverloads constructor(
    name: String? = null,
    private val daemon: Boolean = true
) : ThreadFactory {
    private val threadNumber = AtomicLong()
    private val name = name ?: "KThread"

    override fun newThread(r: Runnable): Thread {
        val thread = KtThread(r, "$name-${threadNumber.getAndIncrement()}")
        thread.isDaemon = daemon
        return thread
    }
}