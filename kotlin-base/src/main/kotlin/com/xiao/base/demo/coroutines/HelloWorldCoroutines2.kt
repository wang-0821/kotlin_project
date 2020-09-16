package com.xiao.base.demo.coroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *
 * @author lix wang
 */
class HelloWorldCoroutines2 {
    inline fun printData(crossinline printer: () -> Unit) {
        println("print data start")
        printer()
        println("print data over")
    }

    fun printFun() {
        printData {
            println("print data")
            return@printData
        }
        println("printer end")
    }
}

fun main() = runBlocking {
    launch {
        delay(200L)
        println("Task from runBlocking")
    }
    coroutineScope {
        launch {
            delay(500L)
            println("Task from nested launch")
        }
        delay(100L)
        println("Task from coroutine scope")
    }
    println("Coroutine scope is over")
//    printFun()
}