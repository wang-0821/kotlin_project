package xiao.http.io

import xiao.http.CloseableResource
import xiao.http.Route

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

    fun readTimeout(timeout: Int)

    fun writeTimeout(timeout: Int)
}