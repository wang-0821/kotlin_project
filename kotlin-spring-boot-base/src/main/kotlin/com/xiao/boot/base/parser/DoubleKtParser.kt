package com.xiao.boot.base.parser

/**
 *
 * @author lix wang
 */
object DoubleKtParser : KtParser<Double> {
    override fun parse(value: String): Double {
        return value.toDouble()
    }
}