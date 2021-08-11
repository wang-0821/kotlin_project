package xiao.redis.client.testing

import io.lettuce.core.RedisFuture
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.output.BooleanOutput
import io.lettuce.core.output.IntegerOutput
import io.lettuce.core.output.ValueOutput
import io.lettuce.core.protocol.AsyncCommand
import io.lettuce.core.protocol.Command
import io.lettuce.core.protocol.CommandType
import java.nio.ByteBuffer

/**
 *
 * @author lix wang
 */
class TestingRedisAsyncService : AbstractTestingRedisAsyncService() {
    private val redisService = TestingRedisService()

    override fun get(key: String?): RedisFuture<String> {
        return toStringRedisFuture(redisService.get(key), CommandType.GET)
    }

    override fun set(key: String?, value: String?): RedisFuture<String> {
        return toStringRedisFuture(redisService.set(key, value), CommandType.SET)
    }

    override fun setex(key: String?, seconds: Long, value: String?): RedisFuture<String> {
        return toStringRedisFuture(redisService.setex(key, seconds, value), CommandType.SETEX)
    }

    override fun del(vararg keys: String?): RedisFuture<Long> {
        return toLongRedisFuture(redisService.del(*keys), CommandType.DEL)
    }

    override fun expire(key: String?, seconds: Long): RedisFuture<Boolean> {
        return toBooleanRedisFuture(redisService.expire(key, seconds), CommandType.EXPIRE)
    }

    private fun toBooleanRedisFuture(value: Boolean, commandType: CommandType): RedisFuture<Boolean> {
        val output = BooleanOutput(StringCodec.UTF8)
        output.set(value)
        return AsyncCommand(Command(commandType, output))
            .apply {
                complete()
            }
    }

    private fun toStringRedisFuture(value: String?, commandType: CommandType): RedisFuture<String> {
        val output = ValueOutput(StringCodec.UTF8)
        output.set(
            value
                ?.let {
                    ByteBuffer.wrap(it.toByteArray())
                }
        )
        return AsyncCommand(Command(commandType, output))
            .apply {
                complete()
            }
    }

    private fun toLongRedisFuture(value: Long, commandType: CommandType): RedisFuture<Long> {
        val output = IntegerOutput(StringCodec.UTF8)
        output.set(value)
        return AsyncCommand(Command(commandType, output))
            .apply {
                complete()
            }
    }
}