package com.xiao.base.util

import sun.misc.Unsafe

/**
 *
 * @author lix wang
 */
object UnsafeUtils {
    @JvmStatic
    fun getUnsafe(): Unsafe {
        val unsafeField = Unsafe::class.java.getDeclaredField("theUnsafe")
        unsafeField.isAccessible = true
        return unsafeField.get(null) as Unsafe
    }
}