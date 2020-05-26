package com.xiao.rpc.handler

/**
 *
 * @author lix wang
 */
interface ExecuteHandler : Handler {
    fun onConnectTimeout()

    fun onReadTimeout()

    fun onWriteTimeout()

    fun onException()
}