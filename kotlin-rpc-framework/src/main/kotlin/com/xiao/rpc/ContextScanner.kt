package com.xiao.rpc

import com.xiao.base.resource.PathResourceResolver

/**
 *
 * @author lix wang
 */
object ContextScanner {
    private var refreshed = false

    @Synchronized fun doScan() {
        if (refreshed) {
            return
        }
        PathResourceResolver().scanByPackage(getPackageName(this::javaClass.name))
    }

    private fun getPackageName(fqClassName: String): String {
        val lastDotIndex: Int = fqClassName.lastIndexOf(".")
        return if (lastDotIndex != -1) fqClassName.substring(0, lastDotIndex) else ""
    }
}