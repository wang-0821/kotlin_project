package com.xiao.redis.schedule

/**
 *
 * @author lix wang
 */
annotation class ScheduledTask(
    val initialTime: String = "",
    val fixedRateMills: Long = 0,
    val fixedDelayMills: Long = 0
)