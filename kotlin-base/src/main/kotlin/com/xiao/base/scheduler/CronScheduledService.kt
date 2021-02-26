package com.xiao.base.scheduler

import java.lang.reflect.Method

/**
 *
 * @author lix wang
 */
interface CronScheduledService {
    fun start()

    fun execScheduledMethod(method: Method)

    fun execScheduledMethod(
        initialMills: Long,
        fixedDelayedMills: Long,
        fixedRateMills: Long,
        method: Method
    )
}