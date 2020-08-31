package com.xiao.rpc.cleaner

import com.xiao.rpc.annotation.AutoClean

/**
 *
 * @author lix wang
 */
@AutoClean(period = SocketCleaner.CLEANUP_PERIOD)
class SocketCleaner : Cleaner {
    override fun cleanup() {
    }

    companion object {
        const val CLEANUP_PERIOD = 60 * 1000
    }
}