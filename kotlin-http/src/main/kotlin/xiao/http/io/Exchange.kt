package xiao.http.io

import xiao.http.Route

/**
 *
 * @author lix wang
 */
class Exchange {
    lateinit var address: xiao.http.Address
    var routes: List<Route>? = null
    var connection: Connection? = null
    var connectTimeout: Int = -1
    var readTimeout: Int = -1
    var writeTimeout: Int = -1
}