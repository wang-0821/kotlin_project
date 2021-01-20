package com.xiao.redis.client

import com.xiao.base.util.awaitNanos
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.output.ValueOutput
import io.lettuce.core.protocol.AsyncCommand
import io.lettuce.core.protocol.Command
import io.lettuce.core.protocol.CommandType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

/**
 *
 * @author lix wang
 */
class RedisClientTest {
    @Test
    fun `test redis sync commands`() {
        val redisSyncCommands = RedisHelper.getRedisService(REDIS_URL)
        redisSyncCommands.set(KEY, VALUE)
        Assertions.assertEquals(redisSyncCommands.get(KEY), VALUE)
    }

    @Test
    fun `test redis async commands`() {
        val redisAsyncCommands = RedisHelper.getRedisAsyncService(REDIS_URL)
        redisAsyncCommands.set(KEY, VALUE).get()
        Assertions.assertEquals(redisAsyncCommands.get(KEY).get(), VALUE)
    }

    @Test
    fun `test redis coroutine commands`() {
        runBlocking {
            val redisAsyncCommands = RedisHelper.getRedisAsyncService(REDIS_URL)
            val setCompletableDeferred = redisAsyncCommands.set(KEY, VALUE).suspend()
            setCompletableDeferred.awaitNanos()
            val getCompletableDeferred = redisAsyncCommands.get(KEY).suspend()
            Assertions.assertEquals(getCompletableDeferred.awaitNanos(), VALUE)
        }
    }

    @Test
    fun `test redisFuture to suspend whenComplete block is asynchronous`() {
        runBlocking {
            val output = ValueOutput(StringCodec.UTF8)
            output.set(ByteBuffer.wrap("Hello world!".toByteArray()))
            val redisFuture = AsyncCommand(Command(CommandType.GET, output))
            val deferred = redisFuture.suspend()
            Assertions.assertFalse(deferred.isCompleted)
            redisFuture.complete()
            Assertions.assertTrue(deferred.isCompleted)
            Assertions.assertEquals(deferred.getCompleted(), "Hello world!")
        }
    }

    companion object {
        private const val REDIS_URL = "redis://localhost:6379"
        private const val KEY = "Hello"
        private const val VALUE = "world!"
    }
}