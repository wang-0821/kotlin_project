package com.xiao.boot.base.parser

/**
 *
 * @author lix wang
 */
object ShortKtParser : KtParser<Short> {
    override fun parse(value: String): Short {
        return value.toShort()
    }
}