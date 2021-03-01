package com.xiao.base.scheduler

/**
 *
 * @author lix wang
 */
annotation class ScheduledTask(
    val initialTime: String = "",
    val initial: Long = 0,
    val fixedRate: Long = 0,
    val fixedDelay: Long = 0
)