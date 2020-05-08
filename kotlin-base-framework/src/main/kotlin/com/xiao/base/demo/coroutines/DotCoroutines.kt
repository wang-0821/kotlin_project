package com.xiao.base.demo.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *
 * @author lix wang
 */
fun testPrint() = runBlocking {
    GlobalScope.launch {
        repeat(1000) { i ->
            println("I'm sleeping $i...")
            delay(500L)
        }
    }
    delay(1300L)
}

fun main() = runBlocking {
    // 启动了10万个协程
    testPrint()

    repeat(100_000) {
        launch {
            delay(1000L)
            print(".")
        }
    }
}