package com.xiao.base.util

import com.fasterxml.jackson.databind.ObjectMapper

/**
 *
 * @author lix wang
 */
object JsonUtils {
    private val objectMapper = ObjectMapper()

    fun serialize(obj: Any): String {
        return objectMapper.writeValueAsString(obj)
    }
}