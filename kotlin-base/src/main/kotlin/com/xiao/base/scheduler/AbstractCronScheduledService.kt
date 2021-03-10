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
        var initial: Long = if (scheduledCorn.initialTime.isBlank()) {
            0
        } else {
            val currentSeconds = System.currentTimeMillis() / 1000
            JodaTimeUtils.fromUtcString(scheduledCorn.initialTime).millis / 1000 - currentSeconds
        }

        if (initial <= 0) {
            initial = scheduledCorn.initial
        }

        execScheduledMethod(initial, scheduledCorn.fixedDelay, scheduledCorn.fixedRate, method)
    }

    fun execScheduledMethod(
        initial: Long,
        fixedDelay: Long,
        fixedRate: Long,
        method: Method,
        scheduler: BaseScheduler
    ) {
        if (initial > 0) {
            scheduler.scheduleWithFixedDelay(
                Duration.ofSeconds(initial),
                Duration.ofSeconds(fixedDelay)
            ) {
                ProxyUtils.invoke(this, method, arrayOf())
            }
        } else {
            if (fixedRate > 0) {
                scheduler.scheduleAtFixedRate(
                    Duration.ofSeconds(initial),
                    Duration.ofSeconds(fixedRate)
                ) {
                    ProxyUtils.invoke(this, method, arrayOf())
                }
            } else {
                scheduler.schedule(Duration.ofSeconds(initial)) {
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