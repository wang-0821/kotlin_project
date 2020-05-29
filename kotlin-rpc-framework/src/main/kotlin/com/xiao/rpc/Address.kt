package com.xiao.rpc

/**
 *
 * @author lix wang
 */
open class Address(val protocol: ProtocolType, val host: String) {
    /**
     * get request port
     */
    var port: Int = -1
        get() {
            return if (field > 0) {
                field
            } else {
                protocol.port
            }
        }
}