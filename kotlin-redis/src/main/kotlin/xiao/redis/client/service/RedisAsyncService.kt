package xiao.redis.client.service

import io.lettuce.core.api.async.RedisAsyncCommands

/**
 *
 * @author lix wang
 */
interface RedisAsyncService : RedisAsyncCommands<String, String>