package com.xiao.boot.base.parser

/**
 *
 * @author lix wang
 */
object FloatKtParser : KtParser<Float> {
    override fun parse(value: String): Float {
        return value.toFloat()
    }
}