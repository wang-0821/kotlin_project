package com.xiao.rpc.protocol

import java.net.Socket

/**
 *
 * @author lix wang
 */
class Request {
    constructor(protocol: ProtocolType, host: String) {
        this.route = Route(protocol, host)
    }

    val route: Route
    var requestParams: Map<String, String>? = null
    var method: RequestMethod = RequestMethod.GET
    var header: Map<String, String>? = null
}

fun main() {
    Socket("", 8888)
}