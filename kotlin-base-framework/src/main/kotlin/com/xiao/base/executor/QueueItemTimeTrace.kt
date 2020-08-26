package com.xiao.base.executor

/**
 *
 * @author lix wang
 */
class QueueItemTimeTrace {
    var submitStartTime: Long = 0
    var submitEndTime: Long = 0
    var executeStartTime: Long = 0
    var executeEndTime: Long = 0

    var retries: List<QueueItemTimeTrace>? = null
}