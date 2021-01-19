package com.xiao.base

/**
 *
 * @author lix wang
 */
object ExecutionHelper {
    fun <T : Any?> retryableExec(retryTimes: Int = 2, block: () -> T): T {
        var throwable: Throwable? = null
        for (i in 1..retryTimes + 1) {
            try {
                return block()
            } catch (t: Throwable) {
                throwable = t
            }
        }
        throw throwable!!
    }
}