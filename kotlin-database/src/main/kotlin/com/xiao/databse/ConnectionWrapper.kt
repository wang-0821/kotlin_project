package com.xiao.databse

import java.sql.Connection
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
class ConnectionWrapper(val connection: Connection) {
    var timeout: Long = -1
    var timeUnit: TimeUnit = TimeUnit.MILLISECONDS
}