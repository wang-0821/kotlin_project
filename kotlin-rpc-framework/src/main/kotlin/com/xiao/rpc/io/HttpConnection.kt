package com.xiao.rpc.io

import com.xiao.rpc.RunningState
import com.xiao.rpc.StateSocket
import com.xiao.rpc.validate

/**
 *
 * @author lix wang
 */
class HttpConnection(private val socket: StateSocket) : Connection {
    private val state = RunningState()

    override fun validate() = this.state.validate()
}