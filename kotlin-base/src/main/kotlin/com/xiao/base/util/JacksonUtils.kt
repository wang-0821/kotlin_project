package com.xiao.base.util

import com.fasterxml.jackson.databind.ObjectMapper

/**
 *
 * @author lix wang
 */
object JacksonUtils {
    private val objectMapper = ObjectMapper()

    fun serialize(obj: Any): String {
        return objectMapper.writeValueAsString(obj)
    }
}