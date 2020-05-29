package com.xiao.rpc.handler

import com.xiao.base.exception.KtException
import com.xiao.rpc.Response

/**
 *
 * @author lix wang
 */
class CacheHandler(override val chain: Chain) : ExceptionHandler {
    override fun handle(): Response {
        return Response()
    }

    override fun onConnectTimeout() {
    }

    override fun onReadTimeout() {
    }

    override fun onWriteTimeout() {
    }

    override fun onException(exception: KtException) {
    }
}