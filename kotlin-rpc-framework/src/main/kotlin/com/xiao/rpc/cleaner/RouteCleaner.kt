package com.xiao.rpc.cleaner

import com.xiao.base.context.Context
import com.xiao.rpc.annotation.AutoClean
import com.xiao.rpc.context.RouteContext

/**
 *
 * @author lix wang
 */
@AutoClean(period = RouteCleaner.CLEANUP_PERIOD)
class RouteCleaner : Cleaner {
    override fun cleanup(context: Context) {
        val routeConnection = context as RouteContext? ?: return
        println("Clean up routes.")
    }

    companion object {
        const val CLEANUP_PERIOD = 60 * 1000
    }
}