package com.xiao.boot.base.parser

import com.xiao.base.util.JsonUtils

/**
 *
 * @author lix wang
 */
class ListJsonKtParser<E>(
    private val elementType: Class<E>
) : KtParser<List<E>> {
    override fun parse(value: String): List<E> {
        return JsonUtils.deserializeList(value, elementType)
    }
}