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

    @JvmStatic
    fun <T : Any?> deserialize(content: String, clazz: Class<T>): T {
        return objectMapper.readValue(content, clazz)
    }

    @JvmStatic
    fun <T : Any> deserializeList(content: String, clazz: Class<T>): List<T> {
        val typeReference = objectMapper.typeFactory.constructCollectionType(List::class.java, clazz)
        return objectMapper.readValue(content, typeReference)
    }

    @JvmStatic
    fun <K, V> deserializeMap(content: String, clazzK: Class<K>, clazzV: Class<V>): Map<K, V> {
        return objectMapper.readValue(
            content, objectMapper.typeFactory.constructMapType(HashMap::class.java, clazzK, clazzV)
        )
    }
}