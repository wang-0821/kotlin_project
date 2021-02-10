package com.xiao.redis.schedule

import com.xiao.base.util.JodaTimeUtils
import com.xiao.base.util.ProxyUtils
import java.lang.reflect.Method
import java.time.Duration

/**
 *
 * @author lix wang
 */
abstract class CornRedisLockScheduler {
    private val redisLockScheduler: RedisLockScheduler
    private val scheduledMethods: List<Method>

    constructor(redisLockScheduler: RedisLockScheduler) {
        this.redisLockScheduler = redisLockScheduler
        this.scheduledMethods = parseScheduledMethod()
    }

    fun start() {
        if (scheduledMethods.isEmpty()) {
            throw IllegalStateException("No accessible methods annotated by ${ScheduledCorn::class.java.simpleName}.")
        }

        for (method in scheduledMethods) {
            val scheduledCorn = method.getAnnotation(ScheduledCorn::class.java)
            val initialMills: Long = if (scheduledCorn.initialTime.isBlank()) {
                0
            } else {
                val currentMills = System.currentTimeMillis()
                JodaTimeUtils.fromUtcString(scheduledCorn.initialTime).millis - currentMills
            }
            // exec scheduled method
            execScheduledMethod(initialMills, scheduledCorn.fixedDelayMills, scheduledCorn.fixedRateMills, method)
        }
    }

    private fun execScheduledMethod(
        initialMills: Long,
        fixedDelayedMills: Long,
        fixedRateMills: Long,
        method: Method
    ) {
        if (fixedDelayedMills > 0) {
            redisLockScheduler.scheduleWithFixedDelay(
                Duration.ofMillis(initialMills),
                Duration.ofMillis(fixedDelayedMills)
            ) {
                ProxyUtils.invoke(this, method, arrayOf())
            }
        } else {
            if (fixedRateMills > 0) {
                redisLockScheduler.scheduleAtFixedRate(
                    Duration.ofMillis(initialMills),
                    Duration.ofMillis(fixedRateMills)
                ) {
                    ProxyUtils.invoke(this, method, arrayOf())
                }
            } else {
                redisLockScheduler.schedule(Duration.ofMillis(initialMills)) {
                    ProxyUtils.invoke(this, method, arrayOf())
                }
            }
        }
    }

    private fun parseScheduledMethod(): List<Method> {
        return this::class.java.methods
            .filter {
                it.getAnnotation(ScheduledCorn::class.java) != null &&
                    it.isAccessible &&
                    it.parameterCount == 0
            }
    }
}