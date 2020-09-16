package com.xiao.rpc.util

import kotlin.reflect.KClass

/**
 *
 * @author lix wang
 */
fun KClass<*>.packageName(): String {
    val name = this.java.name
    val lastDotIndex: Int = name.lastIndexOf(".")
    return if (lastDotIndex != -1) name.substring(0, lastDotIndex) else ""
}