package com.xiao.base.scheduler

import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
annotation class ScheduledTask(
    val initialTime: String = "",
    val initial: Long = 0,
    val fixedRate: Long = 0,
    val fixedDelay: Long = 0,
    val timeUnit: TimeUnit
)