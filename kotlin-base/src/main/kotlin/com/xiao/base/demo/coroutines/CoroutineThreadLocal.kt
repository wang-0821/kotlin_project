package com.xiao.base.demo.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

/**
 *
 * @author lix wang
 */
class CoroutineThreadLocal {
    fun testThreadLocal() = runBlocking {
        val threadLocal = ThreadLocal<Any>()
        threadLocal.set("main")
        println("Pre-main current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
            println("Launch start, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
            yield()
            println("After yield, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        }
        job.join()
        println("Post-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
    }
}


fun main() {
    val obj = CoroutineThreadLocal()
    obj.testThreadLocal()
}