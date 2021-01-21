package com.xiao.base

import com.xiao.base.util.ThreadUtils

/**
 *
 * @author lix wang
 */
object ExecutionHelper {
    @JvmStatic
    @Throws(Throwable::class)
    fun <T : Any?> retryableExec(
        retryTimes: Int = 2,
        retryMills: Long = 500,
        block: () -> T
    ): T {
        var throwable: Throwable? = null
        for (i in 1..retryTimes + 1) {
            try {
                return block()
            } catch (t: Throwable) {
                throwable = t
                if (i <= retryTimes) {
                    ThreadUtils.safeSleep(retryMills)
                }
            }
        }
        throw throwable!!
    }
}