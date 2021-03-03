package com.xiao.base.thread

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

/**
 * 实际项目中，如果JVM中已经不存在其他用户线程，用户自己创建的线程是否还有必要继续执行？
 * 这里使用守护线程，避免死循环导致JVM无法退出。
 *
 * @author lix wang
 */
class NamedThreadFactory : ThreadFactory {
    private val threadNumber = AtomicLong()
    private val name: String
    private val daemon: Boolean

    @JvmOverloads
    constructor(name: String? = null, daemon: Boolean = true) {
        this.name = name ?: "KThread"
        this.daemon = daemon
    }

    override fun newThread(r: Runnable): Thread {
        val thread = Thread(r, "$name-${threadNumber.getAndIncrement()}")
        thread.isDaemon = daemon
        return thread
    }
}