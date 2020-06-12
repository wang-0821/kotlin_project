package com.xiao.rpc.handler

import com.xiao.rpc.Route
import com.xiao.rpc.io.Connection

/**
 *
 * @author lix wang
 */
class Exchange {
    var routes: Set<Route>? = null
    var connection: Connection? = null
}