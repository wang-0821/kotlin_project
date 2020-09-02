package com.xiao.rpc.cleaner

import com.xiao.base.context.Context
import com.xiao.rpc.annotation.AutoClean
import com.xiao.rpc.context.ConnectionContext

/**
 *
 * @author lix wang
 */
@AutoClean
class ConnectionCleaner : Cleaner {
    override fun cleanup(context: Context) {
        val connectionContext = context as ConnectionContext? ?: return
        println("Clean up connections.")
    }
}