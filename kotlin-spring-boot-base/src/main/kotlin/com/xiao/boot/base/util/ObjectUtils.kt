package util

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 *
 * @author lix wang
 */
fun Type.isByte() = run {
    this == Byte::class.java || this == Byte::class.javaPrimitiveType
}

fun Type.isBoolean() = run {
    this == Boolean::class.java || this == Boolean::class.javaPrimitiveType
}

fun Type.isChar() = run {
    this == Char::class.java || this == Char::class.javaPrimitiveType
}

fun Type.isShort() = run {
    this == Short::class.java || this == Short::class.javaPrimitiveType
}

fun Type.isInt() = run {
    this == Int::class.java || this == Int::class.javaPrimitiveType
}

fun Type.isLong() = run {
    this == Long::class.java || this == Long::class.javaPrimitiveType
}

fun Type.isFloat() = run {
    this == Float::class.java || this == Float::class.javaPrimitiveType
}

fun Type.isDouble() = run {
    this == Double::class.java || this == Double::class.javaPrimitiveType
}

fun Type.isList() = run {
    if (this is ParameterizedType) {
        this.rawType == List::class.java
    } else {
        this == List::class.java
    }
}

fun Type.isMap() = run {
    if (this is ParameterizedType) {
        this.rawType == Map::class.java
    } else {
        this == Map::class.java
    }
}

fun Char.isViewable() = run {
    this.toInt() in 33..126
}