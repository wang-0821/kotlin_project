package com.xiao.redis.client.schedule

import com.xiao.base.executor.BaseScheduledExecutor
import com.xiao.base.executor.DefaultExecutorServiceFactory
import com.xiao.redis.client.RedisHelper
import com.xiao.redis.utils.RedisLock
import com.xiao.redis.utils.SharedRedisLock
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService

/**
 *
 * @author lix wang
 */
class PrintScheduledService : BaseScheduledExecutor {
    constructor(
        name: String,
        redisLock: RedisLock,
        scheduledExecutorService: ScheduledExecutorService
    ) : super(name, scheduledExecutorService)
}

fun main() {
    val redisService = RedisHelper.getRedisService("redis://localhost:6379")
    val redisLock = SharedRedisLock("printLock", "lock", redisService)
    val scheduledExecutorService = DefaultExecutorServiceFactory.newScheduledExecutorService(1)
    val printScheduledService = PrintScheduledService("PrintScheduledTask", redisLock, scheduledExecutorService)

    printScheduledService.schedule(Duration.ofSeconds(2)) {
        println("Hello world!")
    }

    printScheduledService.scheduleAtFixedRate(Duration.ofSeconds(2), Duration.ofSeconds(2)) {
        println("Hello at fixed rate!")
    }

    printScheduledService.scheduleWithFixedDelay(Duration.ofSeconds(2), Duration.ofSeconds(2)) {
        println("Hello with fixed delay!")
    }
}