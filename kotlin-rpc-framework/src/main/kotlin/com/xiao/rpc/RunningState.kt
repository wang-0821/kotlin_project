package com.xiao.rpc

import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author lix wang
 */
class RunningState {
    private val state = AtomicInteger(INITIAL)

    fun state() = this.state.get()

    fun validateAndUse(): Boolean {
        var result = false
        if (state() < RUNNING) {
            result = changeState(RUNNING)
        }
        return result
    }

    fun changeState(updateState: Int, originState: Int? = null, block: (() -> Unit)? = null): Boolean {
        val currentState = originState ?: state()
        if (block != null) {
            block()
        }
        return this.state.compareAndSet(currentState, updateState)
    }

    companion object {
        val INITIAL = -1
        val READY = 0
        val RUNNING = 1
        val TERMINATE = 2
    }
}