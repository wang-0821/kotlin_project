package com.xiao.base.logging

/**
 *
 * @author lix wang
 */
enum class LoggerType(val text: String) {
    NULL(""),
    RPC("rpc"),
    DATA_SOURCE("dataSource"),
    TEST_DATA_SOURCE("testDataSource")
}