package com.xiao.rpc

/**
 *
 * @author lix wang
 */
class RunningState {
    @Volatile private var state: Int = INITIAL
    var lastUsingMills: Long = System.currentTimeMillis()

    fun updateState(state: Int) {
        this.state = state
    }

    fun updateState(originState: Int, newState: Int): Boolean {
        return if (state == originState) {
            state = newState
            true
        } else {
            false
        }
    }

    fun state() = state

    companion object {
        val INITIAL = -1
        val READY = 0
        val RUNNING = 1
        val TERMINATE = 2
    }
}