package com.xiao.base.scheduler

import java.lang.reflect.Method
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
        initialMills: Long,
        fixedDelayedMills: Long,
        fixedRateMills: Long,
        method: Method
    ) {
        execScheduledMethod(initialMills, fixedDelayedMills, fixedRateMills, method, scheduler)
    }

    override fun shutdown() {
        scheduler.shutdown()
    }
}