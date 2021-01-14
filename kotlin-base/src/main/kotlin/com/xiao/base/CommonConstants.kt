package com.xiao.base

/**
 *
 * @author lix wang
 */
object CommonConstants {
    const val CLASS_SUFFIX = ".class"

    @JvmStatic
    fun absolutePath(): String {
        return Thread.currentThread().contextClassLoader.getResource("")?.path ?: ""
    }
}