package xiao.boot.base.parser

import xiao.base.util.JsonUtils

/**
 *
 * @author lix wang
 */
object MapJsonKtParser : KtMapParser {
    override fun <K, V> parse(value: String, keyClass: Class<K>, valueClass: Class<V>): Map<K, V> {
        return JsonUtils.deserializeMap(value, keyClass, valueClass)
    }
}