package com.xiao.demo.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *
 * @author lix wang
 */
class DotCoroutines  {
    fun testPrint() = runBlocking {
        GlobalScope.launch {
            repeat(1000) { i ->
                println("I'm sleeping $i...")
                delay(500L)
            }
        }
        delay(1300L)
    }

    fun testGlobalScope() = GlobalScope.launch {
        println("Hello")
    }
}


fun main() {
    val obj = DotCoroutines()
    // 启动了10万个协程
//    testPrint()
        obj.testGlobalScope()

//    repeat(100_000) {
//        launch {
//            delay(1000L)
//            print(".")
//        }
//    }
}