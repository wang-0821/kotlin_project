package xiao.boot.base.parser

import xiao.base.util.JsonUtils

/**
 *
 * @author lix wang
 */
object ListJsonKtParser : KtListParser {
    override fun <T> parse(value: String, elementClass: Class<T>): List<T> {
        return JsonUtils.deserializeList(value, elementClass)
    }
}