package com.xiao.rpc

import java.net.Socket

/**
 *
 * @author lix wang
 */
class StateSocket(val route: Route) : Socket() {
    val state = RunningState()
}

fun StateSocket.validate(): Boolean {
    return this.isConnected && !this.isClosed && this.state.validate()
}