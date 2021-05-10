package com.xiao.base.util

import sun.misc.Unsafe

/**
 *
 * @author lix wang
 */
object UnsafeUtils {
    @JvmField
    val UNSAFE = kotlin.run {
        val unsafeField = Unsafe::class.java.getDeclaredField("theUnsafe")
        unsafeField.isAccessible = true
        unsafeField.get(null) as Unsafe
    }
}