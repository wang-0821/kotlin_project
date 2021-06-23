package com.xiao.boot.base.parser

import com.xiao.base.util.JsonUtils

/**
 *
 * @author lix wang
 */
object JsonKtObjectParser : KtObjectParser {
    override fun <T> parse(value: String, clazz: Class<T>): T {
        return JsonUtils.deserialize(value, clazz)
    }
}