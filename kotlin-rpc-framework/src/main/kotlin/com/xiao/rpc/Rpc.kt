package com.xiao.rpc

import com.xiao.base.executor.ExecutorUtil
import com.xiao.base.logging.Logging
import com.xiao.rpc.context.DefaultClientContextPool
import com.xiao.rpc.io.Request
import com.xiao.rpc.tool.UrlParser
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
object Rpc: Logging() {
    private val client = Client().apply {
        clientContextPool = DefaultClientContextPool().apply {
            start()
        }
    }

    fun future(name: String, request: Request): Future<String> {
        return ExecutorUtil.submit(name, Callable {
            client.newCall(request).execute().contentAsString()
        })
    }
}

fun main() {
    val time2 = System.currentTimeMillis()
    val futures = mutableListOf<Future<String>>()
    for (i in 1..100) {
        futures.add(Rpc.future("Task-$i", UrlParser.parseUrl("http://www.baidu.com")))
    }
    for (i in futures.indices) {
        val startTime = System.currentTimeMillis()
        try {
            futures[i].get(20, TimeUnit.SECONDS)
        } catch (e: Exception) {
            Rpc.log.error("Future-${i + 1} failed, start at $startTime, end at ${System.currentTimeMillis()}", e)
        }

    }
    println("******** Async rpc consume ${System.currentTimeMillis() - time2} ms ********")
}