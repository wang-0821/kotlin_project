package com.xiao.base.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

/**
 *
 * @author lix wang
 */
object JsonUtils {
    private val objectMapper = ObjectMapper()
        .apply {
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        }

    @JvmStatic
    fun <T : Any?> serialize(obj: T): String {
        return objectMapper.writeValueAsString(obj)
    }
}