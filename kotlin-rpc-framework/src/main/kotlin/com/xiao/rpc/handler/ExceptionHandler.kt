package com.xiao.rpc.handler

import com.xiao.rpc.exception.SocketException

/**
 *
 * @author lix wang
 */
typealias ReadTimeoutHandler = (SocketException.ReadTimeoutExceptionConnection) -> Unit

typealias WriteTimeoutHandler = (SocketException.WriteTimeoutExceptionConnection) -> Unit