package xiao.boot.base.parser

/**
 *
 * @author lix wang
 */
interface KtMapParser {
    fun <K, V> parse(value: String, keyClass: Class<K>, valueClass: Class<V>): Map<K, V>
}