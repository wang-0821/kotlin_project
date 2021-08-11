package xiao.redis.client.service

import io.lettuce.core.api.sync.RedisCommands

/**
 *
 * @author lix wang
 */
interface RedisService : RedisCommands<String, String>