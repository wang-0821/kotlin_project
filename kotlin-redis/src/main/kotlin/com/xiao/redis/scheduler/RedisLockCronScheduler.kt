package com.xiao.redis.scheduler

import com.xiao.base.scheduler.CronScheduler

/**
 *
 * @author lix wang
 */
abstract class RedisLockCronScheduler(
    redisLockScheduler: RedisLockScheduler
) : CronScheduler(redisLockScheduler)