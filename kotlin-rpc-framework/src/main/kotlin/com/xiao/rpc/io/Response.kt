package com.xiao.rpc.io

import com.xiao.rpc.Protocol
import java.io.Closeable

/**
 *
 * @author lix wang
 */
class Response : Closeable {
    /**
     * Http version
     */
    var protocol: Protocol
    /**
     * Http status code
     */
    var status: Int
    /**
     * Http headers
     */
    var headers: List<Header>

    /**
     * Http entity
     */
    var entity: HttpEntity?

    constructor(protocol: Protocol, status: Int, headers: List<Header>, entity: HttpEntity?) {
        this.protocol = protocol
        this.status = status
        this.headers = headers
        this.entity = entity
    }

    override fun close() {
        entity?.close()
    }
}