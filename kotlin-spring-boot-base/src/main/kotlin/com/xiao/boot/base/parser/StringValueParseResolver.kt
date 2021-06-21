package com.xiao.boot.base.parser

import com.xiao.boot.base.util.isBoolean
import com.xiao.boot.base.util.isByte
import com.xiao.boot.base.util.isDouble
import com.xiao.boot.base.util.isFloat
import com.xiao.boot.base.util.isInt
import com.xiao.boot.base.util.isList
import com.xiao.boot.base.util.isLong
import com.xiao.boot.base.util.isMap
import com.xiao.boot.base.util.isShort
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author lix wang
 */
object StringValueParseResolver {
    fun resolve(type: Type, value: String): Any? {
        return if (type == String::class.java) {
            value
        } else {
            if (value.isEmpty()) {
                null
            } else {
                parseValue(type, value)
            }
        }
    }

    private fun parseValue(type: Type, value: String): Any {
        return if (type.isBoolean()) {
            BooleanKtParser.parse(value)
        } else if (type.isByte()) {
            ByteKtParser.parse(value)
        } else if (type.isShort()) {
            ShortKtParser.parse(value)
        } else if (type.isInt()) {
            IntKtParser.parse(value)
        } else if (type.isLong()) {
            LongKtParser.parse(value)
        } else if (type.isFloat()) {
            FloatKtParser.parse(value)
        } else if (type.isDouble()) {
            DoubleKtParser.parse(value)
        } else if (type.isMap()) {
            if (type is ParameterizedType) {
                MapJsonKtParser(
                    type.actualTypeArguments[0] as Class<*>,
                    type.actualTypeArguments[1] as Class<*>
                ).parse(value)
            } else {
                MapJsonKtParser(Any::class.java, Any::class.java).parse(value)
            }
        } else if (type.isList()) {
            if (type is ParameterizedType) {
                ListJsonKtParser(type.actualTypeArguments[0] as Class<*>).parse(value)
            } else {
                ListJsonKtParser(Any::class.java).parse(value)
            }
        } else {
            throw UnsupportedOperationException("Unsupported envProperty parser for ${type.typeName}.")
        }
    }
}