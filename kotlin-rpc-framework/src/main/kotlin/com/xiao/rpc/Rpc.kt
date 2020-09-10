package com.xiao.rpc

import com.xiao.base.executor.ExecutorUtil
import com.xiao.base.logging.Logging
import com.xiao.rpc.context.DefaultClientContextPool
import com.xiao.rpc.io.Request
import com.xiao.rpc.tool.UrlParser
import org.apache.logging.log4j.LogManager
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

    fun call(name: String, request: Request): Future<String> {
        return ExecutorUtil.submit(name, Callable {
            client.newCall(request).execute().contentAsString()
        })
    }
}

fun main() {
    val time1 = System.currentTimeMillis()
//    val client = Client()
//    val request = UrlParser.parseUrl("http://www.baidu.com")
//    for (i in 1..100) {
//        val a = System.currentTimeMillis()
//        client.newCall(request).execute().contentAsString()
//        val b = System.currentTimeMillis()
//        println("Task-$i cost ${b - a} ms")
//        println()
//    }

    Rpc.call("Request baidu.", UrlParser.parseUrl("http://www.baidu.com"))

    Thread.sleep(60000)

//    val logger = LogManager.getLogger("test")
//    val time2 = System.currentTimeMillis()
//
//    println("********* Sync rpc consume ${time2 - time1} ms *********")
//
//    val futures = mutableListOf<Future<String>>()
//    for (i in 1..100) {
//        futures.add(Rpc.call("Task-$i", UrlParser.parseUrl("http://www.baidu.com")))
//    }
//    for (i in futures.indices) {
//        val startTime = System.currentTimeMillis()
//        try {
//            futures[i].get(20, TimeUnit.SECONDS)
//        } catch (e: Exception) {
//            logger.error("Future-${i + 1} failed, start at $startTime, end at ${System.currentTimeMillis()}", e)
//        }
//
//    }
//    println("******** Async rpc consume ${System.currentTimeMillis() - time2} ms ********")
}