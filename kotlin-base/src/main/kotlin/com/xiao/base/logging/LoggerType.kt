package com.xiao.base.logging

/**
 *
 * @author lix wang
 */
enum class LoggerType(val text: String) {
    UNDEFINED("undefined"),
    RPC("rpc"),
    DATA_SOURCE("dataSource"),
    TEST_DATA_SOURCE("testDataSource"),
    REDIS("redis"),
    METRICS("metrics");
}