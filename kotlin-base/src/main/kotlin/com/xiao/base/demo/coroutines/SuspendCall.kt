package com.xiao.base.demo.coroutines

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/**
 *
 * @author lix wang
 */
class SuspendCall {
    suspend fun doSomething1(): Int {
        delay(10000L)
        return 13
    }

    suspend fun doSomething2(): Int {
        delay(1000L)
        return 29
    }

    fun syncSuspendCall() = runBlocking {
        val time = measureTimeMillis {
            val one = doSomething1()
            val two = doSomething2()
            println("The answer is ${one + two}")
        }
        println("Computed in $time ms")
    }

    fun asyncSuspendCall() = GlobalScope.launch {
        val time = measureTimeMillis {
            val one  = async { doSomething1() }
            val two = async { doSomething2() }
            println("The answer is ${one.await() + two.await()}")
            printSuspend()
        }
        println("Computed in $time ms")
    }

    suspend fun printSuspend() {
        println("suspend method")
    }

    fun doSomethingUsefulOneAsync() = GlobalScope.async {
        doSomething1()
    }

    fun doSomethingUsefulTwoAsync() = GlobalScope.async {
        doSomething2()
    }

    fun asyncSuspendCall2() {
        val time = measureTimeMillis {
            val one = doSomethingUsefulOneAsync()
            val two = doSomethingUsefulTwoAsync()
            // 在等待结果时需要调用挂起或者阻塞
            // 使用runBlocking阻塞主线程
            runBlocking {
                println("The answer is ${one.await() + two.await()}")
            }
        }
        println("Computed in $time ms")
    }

    suspend fun computeSum(): Int = coroutineScope {
        val one = async { doSomething1() }
        val two = async { doSomething2() }
        one.await() + one.await()
    }

    fun defineCoroutineName() = runBlocking {
        launch(Dispatchers.Default + CoroutineName("test")) {
            println("I'm working in thread ${Thread.currentThread().name}")
        }
    }
}

fun main() {
    val obj = SuspendCall()
//    obj.syncSuspendCall()
    val job = obj.asyncSuspendCall()
//    obj.asyncSuspendCall2()
//    runBlocking { obj.computeSum() }
//    obj.defineCoroutineName()
}