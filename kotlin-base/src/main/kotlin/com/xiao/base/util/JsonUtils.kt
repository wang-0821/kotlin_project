package com.xiao.base.util

import com.fasterxml.jackson.databind.ObjectMapper

/**
 *
 * @author lix wang
 */
object JsonUtils {
    private val objectMapper = ObjectMapper()

    @JvmStatic
    fun <T : Any?> serialize(obj: T): String {
        return objectMapper.writeValueAsString(obj)
    }
}