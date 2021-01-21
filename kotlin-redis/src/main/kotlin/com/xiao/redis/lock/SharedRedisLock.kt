package com.xiao.redis.lock

import com.xiao.base.CommonConstants
import com.xiao.base.logging.Logging
import com.xiao.base.util.ThreadUtils
import com.xiao.redis.client.service.RedisService
import java.time.Duration

/**
 *
 * @author lix wang
 */
class SharedRedisLock(
    private val lockName: String,
    private val lockValue: String,
    private val redisService: RedisService
) {
    val isLocked: Boolean
        get() {
            return redisService.get(lockName) == lockValue
        }

    fun tryLock(duration: Duration): Boolean {
        try {
            val value = redisService.get(lockName)
            if (value == null) {
                return CommonConstants.STATUS_OK == redisService.setex(lockName, duration.seconds, lockValue)
            } else {
                if (value != lockName) {
                    return false
                } else {
                    redisService.expire(lockName, duration.seconds)
                    if (redisService.get(lockName) != lockValue) {
                        return false
                    }
                    return true
                }
            }
        } catch (e: Exception) {
            log.error("Redis lock tryLock failed, ${e.message}.", e)
            return false
        }
    }

    fun tryLockWithRetry(expireDuration: Duration, retryTimes: Int, retryDuration: Duration): Boolean {
        for (i in 1..retryTimes + 1) {
            if (tryLock(expireDuration)) {
                return true
            }
            if (i <= retryTimes) {
                ThreadUtils.safeSleep(retryDuration.toMillis())
            }
        }
        return false
    }

    fun unlock(): Boolean {
        return try {
            if (redisService.get(lockName) != lockValue) {
                false
            } else {
                redisService.del(lockName)
                true
            }
        } catch (e: Exception) {
            log.error("Redis lock unlock failed, ${e.message}.", e)
            false
        }
    }

    companion object : Logging()
}