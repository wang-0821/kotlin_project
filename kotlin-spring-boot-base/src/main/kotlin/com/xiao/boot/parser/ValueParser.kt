package com.xiao.boot.parser

/**
 *
 * @author lix wang
 */
interface ValueParser<T : Any> {
    fun parse(value: String): T

    fun classType(): Class<*>
}