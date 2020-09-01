package com.xiao.rpc.cleaner

import com.xiao.base.context.Context

/**
 *
 * @author lix wang
 */
interface Cleaner {
    fun cleanup(context: Context)
}