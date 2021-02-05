package com.xiao.redis.utils

import java.time.Duration

/**
 *
 * @author lix wang
 */
interface RedisLock {
    fun tryLock(expire: Duration): Boolean

    fun tryLock(expire: Duration, waitTimeout: Duration): Boolean

    fun isLocked(): Boolean

    fun unlock(): Boolean
}