package com.xiao.boot.base.parser

/**
 *
 * @author lix wang
 */
object ByteKtParser : KtParser<Byte> {
    override fun parse(value: String): Byte {
        return value.toByte()
    }
}