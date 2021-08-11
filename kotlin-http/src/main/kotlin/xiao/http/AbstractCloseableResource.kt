package xiao.http

/**
 *
 * @author lix wang
 */
abstract class AbstractCloseableResource(private val runningState: xiao.http.RunningState) : xiao.http.CloseableResource {
    override fun tryClose(keepAliveMills: Long): Boolean {
        synchronized(runningState) {
            return if (runningState.state() == xiao.http.RunningState.Companion.RUNNING) {
                false
            } else {
                if (System.currentTimeMillis() - runningState.lastUsingMills > keepAliveMills) {
                    runningState.updateState(xiao.http.RunningState.Companion.TERMINATE)
                    true
                } else {
                    false
                }
            }
        }
    }

    override fun tryUse(): Boolean {
        val originState = runningState.state()
        if (originState > xiao.http.RunningState.Companion.READY) {
            return false
        }
        synchronized(runningState) {
            val result = runningState.updateState(originState, xiao.http.RunningState.Companion.RUNNING)
            if (result) {
                runningState.lastUsingMills = System.currentTimeMillis()
            }
            return result
        }
    }

    override fun unUse(): Boolean {
        val originState = runningState.state()
        if (originState != xiao.http.RunningState.Companion.RUNNING) {
            return false
        }
        synchronized(runningState) {
            return runningState.updateState(originState, xiao.http.RunningState.Companion.READY)
        }
    }
}