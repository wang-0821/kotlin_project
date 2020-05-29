package com.xiao.rpc

/**
 *
 * @author lix wang
 */
class Request(val address: Address) {
    var requestParams: Map<String, String>? = null
    var method: RequestMethod =
        RequestMethod.GET
    var header: Map<String, String>? = null
}