package com.xiao.rpc.io

import com.xiao.base.exception.HttpStatus
import com.xiao.rpc.Protocol
import java.io.Closeable

/**
 *
 * @author lix wang
 */
class Response(
    /** Http version */
    val protocol: Protocol = Protocol.HTTP_1_1,
    /** Http status code */
    val code: Int = HttpStatus.SC_OK,
    val headers: List<Header> = mutableListOf(),
    val entity: HttpEntity?
) : Closeable {
    override fun close() {
        entity?.close()
    }
}