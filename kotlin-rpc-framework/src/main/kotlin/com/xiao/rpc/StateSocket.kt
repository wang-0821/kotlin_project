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
    override fun connect(endpoint: SocketAddress?, timeout: Int) {
        synchronized(state) {
            state.changeState(RunningState.RUNNING) {
                super.connect(endpoint, timeout)
            }
        }
    }

    fun validateAndUse(): Boolean {
        synchronized(state) {
            return state.validateAndUse()
        }
    }

    @Throws(ConnectionException::class)
    fun acquireConnection(): Connection {
        val connectionFactory = ConnectionFactorySelector.select()
        return connectionFactory.create(this)
    }
}