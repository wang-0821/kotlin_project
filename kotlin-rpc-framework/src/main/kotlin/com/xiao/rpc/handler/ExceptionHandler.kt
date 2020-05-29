package com.xiao.rpc.handler

import com.xiao.base.exception.KtException

/**
 *
 * @author lix wang
 */
interface ExceptionHandler : Handler {
    fun onConnectTimeout()

    fun onReadTimeout()

    fun onWriteTimeout()

    fun onException(exception: KtException)
}