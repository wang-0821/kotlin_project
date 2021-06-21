package com.xiao.boot.base.parser

/**
 *
 * @author lix wang
 */
object LongKtParser : KtParser<Long> {
    override fun parse(value: String): Long {
        return value.toLong()
    }
}