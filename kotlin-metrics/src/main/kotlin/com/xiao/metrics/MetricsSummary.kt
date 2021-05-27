package com.xiao.metrics

/**
 *
 * @author lix wang
 */
class MetricsSummary {
    var total: Long = 0
    var times: Int = 0
    var min: Int = Int.MIN_VALUE
    var max: Int = Int.MAX_VALUE
    var avg: Int = -1
    var lastUpdateTime: Long? = null

    fun update(latencies: List<Int>) {
        if (latencies.isNotEmpty()) {
            val orderList = latencies.sorted()
            times = orderList.size
            total += times
            min = orderList[0]
            max = orderList[times - 1]
            avg = orderList[times / 2]
            lastUpdateTime = System.currentTimeMillis()
        }
    }
}