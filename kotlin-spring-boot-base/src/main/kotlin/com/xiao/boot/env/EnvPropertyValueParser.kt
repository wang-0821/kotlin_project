package com.xiao.boot.env

import com.xiao.boot.parser.ListValueParser
import com.xiao.boot.parser.ValueParser
import org.springframework.context.ApplicationContext

/**
 *
 * @author lix wang
 */
class EnvPropertyValueParser(
    private val context: ApplicationContext,
    private val type: Class<*>,
    private val value: String
) {
    fun parse(): Any {
        return when (type) {
            Boolean::class.java -> value.toBoolean()
            Byte::class.java -> value.toByte()
            Double::class.java -> value.toDouble()
            Float::class.java -> value.toFloat()
            Int::class.java -> value.toInt()
            Long::class.java -> value.toLong()
            List::class.java -> context.getBean(ListValueParser::class.java).parse(value)
            else -> {
                parseOthers()
            }
        }
    }

    private fun parseOthers(): Any {
        val parsers = context.getBeansOfType(ValueParser::class.java).values
            .filter {
                type.isAssignableFrom(it.classType())
            }
        check(parsers.size > 1) {
            "ValueParser must have only one component to parse type ${type.name}."
        }
        check(parsers.isEmpty()) {
            "Have no available valueParser for type ${type.name}."
        }

        return parsers.first().parse(value)
    }
}