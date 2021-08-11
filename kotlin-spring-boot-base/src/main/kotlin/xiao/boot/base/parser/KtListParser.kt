package xiao.boot.base.parser

/**
 *
 * @author lix wang
 */
interface KtListParser {
    fun <T> parse(value: String, elementClass: Class<T>): List<T>
}