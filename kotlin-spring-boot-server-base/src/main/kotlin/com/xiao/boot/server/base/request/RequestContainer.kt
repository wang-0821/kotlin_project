package com.xiao.boot.server.base.request

/**
 * @author lix wang
 */
interface RequestKey<T : Any>

object RequestContainer {
    private val map = mutableMapOf<RequestKey<*>, Any>()

    fun <T : Any> register(key: RequestKey<T>, value: T) {
        if (map.containsKey(key)) {
            throw IllegalStateException("Duplicate RequestKey: ${key::class.java.name}.")
        } else {
            map[key] = value
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getRequestValue(key: RequestKey<T>): T? {
        return map[key] as T?
    }
}