package com.xiao.base.executor

/**
 *
 * @author lix wang
 */
interface ExecutorMonitor {
    fun taskCount(): Int

    fun taskCapacity(): Int
}