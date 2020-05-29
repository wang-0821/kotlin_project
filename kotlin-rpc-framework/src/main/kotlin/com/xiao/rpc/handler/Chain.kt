package com.xiao.rpc.handler

import com.xiao.base.exception.KtException
import com.xiao.rpc.Request
import com.xiao.rpc.Response
import com.xiao.rpc.RouteContext
import com.xiao.rpc.exception.ExecuteException

/**
 *
 * @author lix wang
 */
class Chain(val request: Request) {
    private var handlers: MutableList<Handler> = mutableListOf()
    private var index = 0

    fun refreshContext() {
        RouteContext()
    }

    fun initHandlers() {
        handlers.add(EstablishHandler(this))
    }

    @Throws(KtException::class)
    fun execute(): Response {
        if (index > handlers.size) {
            throw ExecuteException.executeOutOfBound()
        }
        return handlers[index++].handle()
    }
}