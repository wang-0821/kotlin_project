package com.xiao.boot.base.parser

import com.xiao.base.util.JsonUtils

/**
 *
 * @author lix wang
 */
class MapJsonKtParser<K, V>(
    private val keyType: Class<K>,
    private val valueType: Class<V>
) : KtParser<Map<K, V>> {
    override fun parse(value: String): Map<K, V> {
        return JsonUtils.deserializeMap(value, keyType, valueType)
    }
}