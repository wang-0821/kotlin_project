package com.xiao.redis.client.service

import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands

/**
 *
 * @author lix wang
 */
interface RedisClusterService : RedisAdvancedClusterCommands<String, String>