package com.xiao.rpc

/**
 *
 * @author lix wang
 */
abstract class AbstractCloseableResource(private val runningState: RunningState) : CloseableResource {
    override fun tryClose(keepAliveMills: Long): Boolean {
        synchronized(runningState) {
            return if (runningState.state() == RunningState.RUNNING) {
                false
            } else {
                if (System.currentTimeMillis() - runningState.lastUsingMills > keepAliveMills) {
                    runningState.updateState(RunningState.TERMINATE)
                    true
                } else {
                    false
                }
            }
        }
    }

    override fun tryUse(): Boolean {
        val originState = runningState.state()
        if (originState > RunningState.READY) {
            return false
        }
        synchronized(runningState) {
            val result = runningState.updateState(originState, RunningState.RUNNING)
            if (result) {
                runningState.lastUsingMills = System.currentTimeMillis()
            }
            return result
        }
    }

    override fun unUse(): Boolean {
        val originState = runningState.state()
        if (originState != RunningState.RUNNING) {
            return false
        }
        synchronized(runningState) {
            return runningState.updateState(originState, RunningState.READY)
        }
    }
}