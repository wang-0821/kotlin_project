package com.xiao.boot.base.parser

/**
 *
 * @author lix wang
 */
object IntKtParser : KtParser<Int> {
    override fun parse(value: String): Int {
        return value.toInt()
    }
}