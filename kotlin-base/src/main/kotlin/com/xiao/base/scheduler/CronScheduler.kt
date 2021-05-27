package com.xiao.base.scheduler

import java.lang.reflect.Method
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService

/**
 *
 * @author lix wang
 */
abstract class CronScheduler : AbstractCronScheduledService {
    private val scheduler: AbstractScheduler

    constructor(scheduler: AbstractScheduler) {
        this.scheduler = scheduler
    }

    constructor(name: String, scheduledExecutorService: ScheduledExecutorService) {
        this.scheduler = object : AbstractScheduler(name, scheduledExecutorService) {}
    }

    override fun execScheduledMethod(
        initial: Duration,
        fixedDelay: Duration,
        fixedRate: Duration,
        method: Method
    ) {
        execScheduledMethod(initial, fixedDelay, fixedRate, method, scheduler)
    }

    override fun shutdown() {
        scheduler.shutdown()
    }
}