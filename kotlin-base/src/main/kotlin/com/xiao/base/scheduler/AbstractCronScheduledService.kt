package com.xiao.base.scheduler

import com.xiao.base.util.JodaTimeUtils
import com.xiao.base.util.ProxyUtils
import java.lang.reflect.Method
import java.time.Duration

/**
 *
 * @author lix wang
 */
abstract class AbstractCronScheduledService : CronScheduledService {
    private val scheduledMethods: List<Method>

    init {
        scheduledMethods = parseScheduledMethod()
    }

    override fun start() {
        if (scheduledMethods.isEmpty()) {
            throw IllegalStateException("No accessible methods annotated by ${ScheduledTask::class.java.simpleName}.")
        }

        for (method in scheduledMethods) {
            // exec scheduled method
            execScheduledMethod(method)
        }
    }

    override fun execScheduledMethod(method: Method) {
        val scheduledCorn = method.getAnnotation(ScheduledTask::class.java)
        var initialMills: Long = if (scheduledCorn.initialTime.isBlank()) {
            0
        } else {
            val currentMills = System.currentTimeMillis()
            JodaTimeUtils.fromUtcString(scheduledCorn.initialTime).millis - currentMills
        }

        if (initialMills <= 0) {
            initialMills = scheduledCorn.timeUnit.toMillis(scheduledCorn.initial)
        }

        val initial = Duration.ofMillis(initialMills)
        val fixedDelay = Duration.ofMillis(scheduledCorn.timeUnit.toMillis(scheduledCorn.fixedDelay))
        val fixedRate = Duration.ofMillis(scheduledCorn.timeUnit.toMillis(scheduledCorn.fixedRate))
        execScheduledMethod(initial, fixedDelay, fixedRate, method)
    }

    fun execScheduledMethod(
        initial: Duration,
        fixedDelay: Duration,
        fixedRate: Duration,
        method: Method,
        scheduler: AbstractScheduler
    ) {
        if (initial.isNegative) {
            scheduler.scheduleWithFixedDelay(initial, fixedDelay) {
                ProxyUtils.invoke(this, method, arrayOf())
            }
        } else {
            if (fixedRate.isNegative) {
                scheduler.scheduleAtFixedRate(initial, fixedDelay) {
                    ProxyUtils.invoke(this, method, arrayOf())
                }
            } else {
                scheduler.schedule(initial) {
                    ProxyUtils.invoke(this, method, arrayOf())
                }
            }
        }
    }

    private fun parseScheduledMethod(): List<Method> {
        return this::class.java.methods
            .filter {
                it.getAnnotation(ScheduledTask::class.java) != null && it.parameterCount == 0
            }
    }
}