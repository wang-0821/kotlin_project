package com.xiao.rpc.cleaner

import com.xiao.rpc.annotation.AutoClean

/**
 *
 * @author lix wang
 */
@AutoClean(period = ConnectionCleaner.CLEANUP_PERIOD)
class ConnectionCleaner : Cleaner {
    override fun cleanup() {
    }

    companion object {
        const val CLEANUP_PERIOD = 30 * 1000
    }
}