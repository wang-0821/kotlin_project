package com.xiao.rpc

import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author lix wang
 */
class RunningState {
    private val state = AtomicInteger(INITIAL)

    fun changeState(origin: Int, current: Int): Boolean {
        return this.state.compareAndSet(origin, current)
    }

    fun state() = this.state.get()

    companion object {
        val INITIAL = -1
        val READY = 0
        val RUNNING = 1
        val TERMINATE = 2
    }
}

fun RunningState.validate(): Boolean {
    val origin = this.state()
    if (origin == RunningState.INITIAL || origin == RunningState.READY) {
        return this.changeState(origin, RunningState.READY)
    }
    return false
}