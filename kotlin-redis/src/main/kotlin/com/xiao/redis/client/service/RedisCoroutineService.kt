package com.xiao.redis.client.service

import io.lettuce.core.api.coroutines.RedisCoroutinesCommands

/**
 *
 * @author lix wang
 */
interface RedisCoroutineService : RedisCoroutinesCommands<String, String>