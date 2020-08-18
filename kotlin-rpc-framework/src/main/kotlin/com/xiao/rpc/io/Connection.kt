package com.xiao.rpc.io

import com.xiao.rpc.Route
import java.io.Closeable

/**
 *
 * @author lix wang
 */
interface Connection : Closeable {
    fun connect()

    fun validateAndUse(): Boolean

    fun route(): Route

    fun writeHeaders(request: Request)

    fun writeBody(request: Request)

    fun write(message: ByteArray)

    fun finishRequest()

    fun response(exchange: Exchange): Response
}