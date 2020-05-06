package com.xiao.base.demo.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *
 * @author lix wang
 */
fun main() = runBlocking {
    launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
//    job.join()
//    delay(2000L)  延迟2秒来保证JVM存活
}