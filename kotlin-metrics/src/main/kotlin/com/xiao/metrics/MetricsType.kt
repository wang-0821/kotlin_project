package com.xiao.metrics

/**
 * Metrics event type.
 *
 * @author lix wang
 */
enum class MetricsType {
    DB,
    DB_SLOW,
    RPC,
    RPC_SLOW,
    API,
    API_SLOW;
}