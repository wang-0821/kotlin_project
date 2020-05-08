package com.xiao.base.demo.coroutines

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/**
 *
 * @author lix wang
 */
suspend fun doSomething1(): Int {
    delay(1000L)
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

fun asyncSuspendCall() = runBlocking {
    val time = measureTimeMillis {
        val one  = async { doSomething1() }
        val two = async { doSomething2() }
        println("The answer is ${one.await() + two.await()}")
    }
    println("Computed in $time ms")
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

fun main() {
    syncSuspendCall()
    asyncSuspendCall()
    asyncSuspendCall2()
    runBlocking { computeSum() }
}