package xiao.redis.scheduler

import xiao.base.scheduler.CronScheduler

/**
 *
 * @author lix wang
 */
abstract class RedisLockCronScheduler(
    redisLockScheduler: RedisLockScheduler
) : CronScheduler(redisLockScheduler)