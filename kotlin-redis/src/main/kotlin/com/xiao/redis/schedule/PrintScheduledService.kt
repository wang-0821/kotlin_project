package com.xiao.redis.schedule

import com.xiao.base.executor.BaseScheduledExecutor
import com.xiao.base.executor.DefaultExecutorServiceFactory
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService

/**
 *
 * @author lix wang
 */
class PrintScheduledService : BaseScheduledExecutor {
    constructor(
        name: String,
        scheduledExecutorService: ScheduledExecutorService
    ) : super(name, scheduledExecutorService)
}

fun main() {
    val scheduledExecutorService = DefaultExecutorServiceFactory.newScheduledExecutorService(1)
    val printScheduledService = PrintScheduledService("PrintScheduledTask", scheduledExecutorService)

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