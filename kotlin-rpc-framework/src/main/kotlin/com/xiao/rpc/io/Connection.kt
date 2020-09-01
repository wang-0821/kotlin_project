package com.xiao.rpc.io

import com.xiao.rpc.CloseableResource
import com.xiao.rpc.Route

/**
 *
 * @author lix wang
 */
interface Connection : CloseableResource {
    fun connect()

    fun route(): Route

    fun writeHeaders(request: Request)

    fun writeBody(request: Request)

    fun write(message: ByteArray)

    fun finishRequest()

    fun response(exchange: Exchange): Response
}