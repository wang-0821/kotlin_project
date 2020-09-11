package com.xiao.rpc

import java.net.InetSocketAddress

/**
 *
 * @author lix wang
 */
class Route(
    val address: Address,
    val inetSocketAddress: InetSocketAddress
) : CloseableResource {
    private val state = RunningState()
    private var lastUsingMills = System.currentTimeMillis()

    override fun tryClose(keepAliveMills: Long): Boolean {
        synchronized(state) {
            return if (System.currentTimeMillis() - lastUsingMills > keepAliveMills) {
                state.updateState(RunningState.TERMINATE)
                true
            } else {
                false
            }
        }
    }

    override fun tryUse(): Boolean {
        synchronized(state) {
            return if (state.state() >= RunningState.TERMINATE) {
                false
            } else {
                lastUsingMills = System.currentTimeMillis()
                true
            }
        }
    }

    override fun unUse(): Boolean {
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Route

        if (address != other.address) return false
        if (inetSocketAddress != other.inetSocketAddress) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + inetSocketAddress.hashCode()
        return result
    }

    override fun toString(): String {
        return "Route(address=$address, inetSocketAddress=$inetSocketAddress)"
    }
}