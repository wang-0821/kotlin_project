package com.xiao.metrics

/**
 * Metrics event type.
 *
 * @author lix wang
 */
enum class MetricsType {
    DB_QUERY_SLOW,
    RPC_REQUEST_SLOW,
    API_SLOW,
    NORMAL
}