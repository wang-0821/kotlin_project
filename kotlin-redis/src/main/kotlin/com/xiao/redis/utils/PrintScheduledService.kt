package com.xiao.redis.utils

import com.xiao.base.executor.ExecutorServiceFactory
import com.xiao.redis.client.RedisHelper
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
class PrintScheduledService : RedisLockScheduledExecutor {
    constructor(
        name: String,
        redisLock: RedisLock,
        scheduledExecutorService: ScheduledExecutorService
    ) : super(name, redisLock, scheduledExecutorService)
}

fun main() {
    val redisService = RedisHelper.getRedisService("redis://localhost:6379")
    val redisLock = SharedRedisLock("printLock", "lock", redisService)
    val scheduledExecutorService = ExecutorServiceFactory.newScheduledExecutorService(1)
    val printScheduledService = PrintScheduledService("PrintScheduledTask", redisLock, scheduledExecutorService)

    printScheduledService.schedule(2L, TimeUnit.SECONDS) {
        println("Hello world!")
    }

    printScheduledService.scheduleAtFixedRate(2, 2, TimeUnit.SECONDS) {
        println("Hello at fixed rate!")
    }

    printScheduledService.scheduleWithFixedDelay(2, 2, TimeUnit.SECONDS) {
        println("Hello with fixed delay!")
    }
}