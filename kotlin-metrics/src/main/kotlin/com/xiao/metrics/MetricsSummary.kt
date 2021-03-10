package com.xiao.metrics

import com.xiao.base.thread.ThreadUnsafe

/**
 *
 * @author lix wang
 */
class MetricsSummary {
    var times: Int = 0
        private set
    var min: Int = Int.MIN_VALUE
        private set
    var max: Int = Int.MAX_VALUE
        private set
    var avg: Int = -1
        private set
    private var latencies: List<Int>? = null

    @ThreadUnsafe
    fun calculateSummary(latencies: List<Int>): MetricsSummary {
        if (latencies.isNotEmpty()) {
            val latencyList = latencies.sorted()
            this.latencies = latencyList
            val size = latencyList.size
            times = latencyList.size
            min = latencyList[0]
            max = latencyList[size - 1]
            avg = latencyList[size / 2]
        }

        return this
    }
}