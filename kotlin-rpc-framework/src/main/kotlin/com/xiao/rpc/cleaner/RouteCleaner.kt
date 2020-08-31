package com.xiao.rpc.cleaner

import com.xiao.rpc.annotation.AutoClean

/**
 *
 * @author lix wang
 */
@AutoClean(period = RouteCleaner.CLEANUP_PERIOD)
class RouteCleaner : Cleaner {
    override fun cleanup() {
    }

    companion object {
        const val CLEANUP_PERIOD = 5 * 60 * 1000
    }
}