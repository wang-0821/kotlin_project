package com.xiao.metrics

/**
 *
 * @author lix wang
 */
class MetricsSummary {
    var total: Long = 0
    var times: Int = 0
    var min: Int = -1
    var max: Int = -1
    var avg: Int = -1
    var per90: Int = -1
    var per99: Int = -1
    var qps: Int = 0
    var lastUpdateTime: Long = System.currentTimeMillis()

    fun update(latencies: List<Int>) {
        val current = System.currentTimeMillis()
        if (latencies.isNotEmpty()) {
            val orderList = latencies.sorted()
            times = orderList.size
            total += times
            min = orderList[0]
            max = orderList[times - 1]
            avg = orderList[times / 2]
            per90 = orderList[(times * 0.9).toInt()]
            per99 = orderList[(times * 0.99).toInt()]
            qps = (times * 1000 / ((current - lastUpdateTime)).coerceAtLeast(1)).toInt()
        }
        lastUpdateTime = current
    }
}