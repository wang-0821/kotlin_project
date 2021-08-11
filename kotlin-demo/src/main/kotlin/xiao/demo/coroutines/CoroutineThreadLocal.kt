package xiao.demo.coroutines

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
        println("Pre-main current xiao.base.thread: ${Thread.currentThread()}, xiao.base.thread local value: '${threadLocal.get()}'")
        val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
            println("Launch start, current xiao.base.thread: ${Thread.currentThread()}, xiao.base.thread local value: '${threadLocal.get()}'")
            yield()
            println("After yield, current xiao.base.thread: ${Thread.currentThread()}, xiao.base.thread local value: '${threadLocal.get()}'")
        }
        job.join()
        println("Post-main, current xiao.base.thread: ${Thread.currentThread()}, xiao.base.thread local value: '${threadLocal.get()}'")
    }
}

fun main() {
    val obj = CoroutineThreadLocal()
    obj.testThreadLocal()
}