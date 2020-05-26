package com.xiao.rpc.handler

import com.xiao.base.exception.KtException
import com.xiao.rpc.exception.ExecuteException
import com.xiao.rpc.protocol.Request
import com.xiao.rpc.protocol.Response

/**
 *
 * @author lix wang
 */
abstract class Chain(val request: Request) {
    private var handlers: MutableList<Handler> = mutableListOf()
    private var index = 0

    fun addHandler(handler: Handler) {
        handlers.add(handler)
    }

    @Throws(KtException::class)
    fun execute(): Response {
        if (index > handlers.size) {
            throw ExecuteException.executeOutOfBound()
        }
        return handlers[index++].handle()
    }
}