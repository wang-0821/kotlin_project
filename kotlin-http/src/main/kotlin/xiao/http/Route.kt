package xiao.http

import java.net.InetSocketAddress

/**
 *
 * @author lix wang
 */
data class Route(
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
}