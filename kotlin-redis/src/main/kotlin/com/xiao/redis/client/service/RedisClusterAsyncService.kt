package com.xiao.redis.client.service

import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands

/**
 *
 * @author lix wang
 */
interface RedisClusterAsyncService : RedisAdvancedClusterAsyncCommands<String, String>