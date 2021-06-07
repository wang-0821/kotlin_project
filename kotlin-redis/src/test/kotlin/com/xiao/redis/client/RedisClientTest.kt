package com.xiao.redis.client

import com.xiao.base.CommonConstants
import com.xiao.base.testing.KtTestBase
import io.lettuce.core.LettuceFutures
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.output.ValueOutput
import io.lettuce.core.protocol.AsyncCommand
import io.lettuce.core.protocol.Command
import io.lettuce.core.protocol.CommandType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
class RedisClientTest : KtTestBase() {
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

    @Test
    fun `test getting multiple key asynchronously`() {
        val redisCommand = RedisHelper.getRedisAsyncService(REDIS_URL)
        val setFutures = (0..9).map { redisCommand.set("key-$it", "value-$it") }
        LettuceFutures.awaitAll(1, TimeUnit.MINUTES, *setFutures.toTypedArray())
        Assertions.assertEquals(
            setFutures.filter { it.isDone && it.get() == CommonConstants.STATUS_OK }.size,
            10
        )

        val results = (0..9).map { redisCommand.get("key-$it") }
        LettuceFutures.awaitAll(1, TimeUnit.MINUTES, *results.toTypedArray())
        Assertions.assertEquals(results.filter { it.isDone && it.get().startsWith("value-") }.size, 10)
    }

    @Test
    fun `test redis future listener`() {
        val command = RedisHelper.getRedisAsyncService(REDIS_URL)
        val setFuture = command.set(KEY, VALUE)
        setFuture.thenRun {
            Assertions.assertEquals(command.get(KEY).get(), VALUE)
        }
    }

    @Test
    fun `test redis transaction`() {
        val command = RedisHelper.getRedisService(REDIS_URL)
        val multiResult = command.multi()
        Assertions.assertEquals(multiResult, CommonConstants.STATUS_OK)
        val setResult = command.set(KEY, VALUE)
        Assertions.assertNull(setResult)
        val execResult = command.exec()
        Assertions.assertEquals(execResult.size(), 1)
        Assertions.assertEquals(execResult[0], CommonConstants.STATUS_OK)
    }

    companion object {
        private const val REDIS_URL = "redis://localhost:6379"
        private const val KEY = "Hello"
        private const val VALUE = "world!"
    }
}