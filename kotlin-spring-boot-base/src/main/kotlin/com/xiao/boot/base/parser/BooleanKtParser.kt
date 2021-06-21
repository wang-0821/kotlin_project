package com.xiao.boot.base.parser

/**
 *
 * @author lix wang
 */
object BooleanKtParser : KtParser<Boolean> {
    override fun parse(value: String): Boolean {
        return value.toBoolean()
    }
}