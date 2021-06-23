package com.xiao.boot.base.parser

/**
 *
 * @author lix wang
 */
interface KtObjectParser {
    fun <T> parse(value: String, clazz: Class<T>): T
}