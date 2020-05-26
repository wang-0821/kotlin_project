package com.xiao.base.demo.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 *
 * @author lix wang
 */
class CoroutineUsage {
    suspend fun useIoDispatcher() = withContext(Dispatchers.IO) {
        delay(100)
        println("use io dispatcher")
    }

    fun bar(x: String?) {
        if (!x.isNullOrEmpty()) {
            println()
        }
    }
}

@ExperimentalContracts
fun require(condition: Boolean) {
    contract {
        returns() implies condition
    }
    if (!condition) throw IllegalArgumentException()
}

@ExperimentalContracts
fun foo(s: String?) {
    require(s is String)
}

suspend fun main() {
    val obj = CoroutineUsage()
    obj.useIoDispatcher()
}