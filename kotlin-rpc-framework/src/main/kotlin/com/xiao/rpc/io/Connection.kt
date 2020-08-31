package com.xiao.rpc.io

import com.xiao.rpc.Route
import com.xiao.rpc.cleaner.Cleaner
import java.io.Closeable

/**
 *
 * @author lix wang
 */
interface Connection : Closeable, Cleaner {
    fun connect()

    /**
     * @return -1 means [Connection] is invalid, 0 means [Connection] is in use, 1 means [Connection] is valid.
     */
    fun validateAndUse(): Int

    fun route(): Route

    fun writeHeaders(request: Request)

    fun writeBody(request: Request)

    fun write(message: ByteArray)

    fun finishRequest()

    fun response(exchange: Exchange): Response
}