package com.xiao.boot.base.parser

/**
 *
 * @author lix wang
 */
interface KtParser<T> {
    fun parse(value: String): T
}