package com.xiao.rpc.io

import com.xiao.rpc.Address
import com.xiao.rpc.Route

/**
 *
 * @author lix wang
 */
class Exchange {
    lateinit var address: Address
    var routes: List<Route>? = null
    var connection: Connection? = null
    var connectTimeout: Int = -1
    var readTimeout: Int = -1
    var writeTimeout: Int = -1
}