package com.xiao.rpc

import com.xiao.rpc.exception.ConnectionException
import com.xiao.rpc.factory.ConnectionFactorySelector
import com.xiao.rpc.io.Connection
import java.io.IOException
import java.net.Socket
import java.net.SocketAddress

/**
 *
 * @author lix wang
 */
class StateSocket(val route: Route) : Socket() {
    private val state = RunningState()

    @Throws(IOException::class)
    @Synchronized override fun connect(endpoint: SocketAddress?, timeout: Int) {
        changeState(RunningState.RUNNING) {
            super.connect(endpoint, timeout)
        }
    }

    @Synchronized fun validateAndUse(): Boolean {
        var result = false
        if (state.state() < RunningState.RUNNING) {
            result = changeState(RunningState.RUNNING)
        }
        return result
    }

    @Throws(ConnectionException::class)
    fun acquireConnection(): Connection {
        val connectionFactory = ConnectionFactorySelector.select()
        return connectionFactory.create(this)
    }

    private fun changeState(updateState: Int, originState: Int? = null, block: (() -> Unit)? = null): Boolean {
        val currentState = originState ?: state.state()
        if (block != null) {
            block()
        }
        return state.changeState(currentState, updateState)
    }
}