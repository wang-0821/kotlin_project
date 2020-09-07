package com.xiao.rpc

import com.xiao.base.executor.ExecutorUtil
import com.xiao.base.executor.WrappedFuture
import com.xiao.rpc.io.Request
import com.xiao.rpc.io.Response
import com.xiao.rpc.tool.UrlParser
import java.util.concurrent.Callable

/**
 *
 * @author lix wang
 */
object Rpc {
    private val client = Client()

    fun call(name: String, request: Request): WrappedFuture<Response> {
        return ExecutorUtil.submit(name, Callable { client.newCall(request).execute() })
    }
}

fun main() {
    val time1 = System.currentTimeMillis()
    val client = Client()
    val request = UrlParser.parseUrl("http://www.baidu.com")
    for (i in 1..100) {
        val a = System.currentTimeMillis()
        client.newCall(request).execute().contentAsString()
        val b = System.currentTimeMillis()
        println("Task-$i cost ${b - a} ms")
        println()
    }
    val time2 = System.currentTimeMillis()

    println("********* Sync rpc consume ${time2 - time1} ms *********")

//    val futures = mutableListOf<WrappedFuture<Response>>()
//    for (i in 1..100) {
//        futures.add(Rpc.call("Task-$i", request))
//    }
//    futures.forEach {
//        it.get().contentAsString()
//    }
//    println("******** Async rpc consume ${System.currentTimeMillis() - time2} ms ********")
}