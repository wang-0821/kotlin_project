package com.xiao.boot.base.parser

import com.xiao.base.util.JsonUtils

/**
 *
 * @author lix wang
 */
class ObjectJsonKtParser<T>(
    private val clazz: Class<T>
) : KtParser<T> {
    override fun parse(value: String): T {
        return JsonUtils.deserialize(value, clazz)
    }
}