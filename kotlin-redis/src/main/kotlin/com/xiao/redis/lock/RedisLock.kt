package com.xiao.redis.lock

import com.xiao.base.CommonConstants
import com.xiao.base.logging.Logging
import com.xiao.redis.client.service.RedisService
import java.time.Duration

/**
 *
 * @author lix wang
 */
class RedisLock(
    private val lockName: String,
    private val lockValue: String,
    private val redisService: RedisService
) {
    fun tryLock(expire: Duration): Boolean {
        try {
            val value = redisService.get(lockName)
            if (value == null) {
                return CommonConstants.STATUS_OK == redisService.setex(lockName, expire.seconds, lockValue)
            } else {
                if (value != lockName) {
                    return false
                } else {
                    redisService.expire(lockName, expire.seconds)
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

    fun tryLock(expire: Duration, waitTimeout: Duration): Boolean {
        val deadline = System.nanoTime() + waitTimeout.toNanos()
        while (true) {
            if (tryLock(expire)) {
                return true
            } else {
                if (System.nanoTime() >= deadline) {
                    return false
                }
            }
        }
    }

    fun isLocked(): Boolean {
        return redisService.get(lockName) == lockValue
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