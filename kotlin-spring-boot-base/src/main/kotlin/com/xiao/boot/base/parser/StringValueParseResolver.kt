package com.xiao.boot.base.parser

import util.isBoolean
import util.isByte
import util.isDouble
import util.isFloat
import util.isInt
import util.isList
import util.isLong
import util.isMap
import util.isShort
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
            value.toBoolean()
        } else if (type.isByte()) {
            value.toByte()
        } else if (type.isShort()) {
            value.toShort()
        } else if (type.isInt()) {
            value.toInt()
        } else if (type.isLong()) {
            value.toLong()
        } else if (type.isFloat()) {
            value.toFloat()
        } else if (type.isDouble()) {
            value.toDouble()
        } else if (type.isMap()) {
            if (type is ParameterizedType) {
                MapJsonKtParser.parse(
                    value,
                    type.actualTypeArguments[0] as Class<*>,
                    type.actualTypeArguments[1] as Class<*>
                )
            } else {
                MapJsonKtParser.parse(value, Any::class.java, Any::class.java)
            }
        } else if (type.isList()) {
            if (type is ParameterizedType) {
                ListJsonKtParser.parse(value, type.actualTypeArguments[0] as Class<*>)
            } else {
                ListJsonKtParser.parse(value, Any::class.java)
            }
        } else {
            (type as? Class<*>)
                ?.let {
                    JsonKtObjectParser.parse(value, it)
                } ?: throw UnsupportedOperationException("Unsupported parse type ${type.typeName}.")
        }
    }
}