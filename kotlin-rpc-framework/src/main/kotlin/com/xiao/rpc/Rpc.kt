package com.xiao.rpc

import com.xiao.base.executor.ExecutorUtil
import com.xiao.base.logging.Logging
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
    private val client = Client()

    fun call(name: String, request: Request): Future<String> {
        return ExecutorUtil.submit(name, Callable {
            try {
                client.newCall(request).execute().contentAsString()
            } catch (e: Exception) {
                log.error("Serialize to string failed.")
                throw e
            }
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
    val logger = LogManager.getLogger("test")
    logger.error("Hello")
    val time2 = System.currentTimeMillis()

    println("********* Sync rpc consume ${time2 - time1} ms *********")

    val futures = mutableListOf<Future<String>>()
    for (i in 1..100) {
        futures.add(Rpc.call("Task-$i", UrlParser.parseUrl("http://www.baidu.com")))
    }
    futures.forEach {
        try {
            it.get(5, TimeUnit.SECONDS)
        } catch (e: Exception) {
            logger.error("Get $it failed.")
        }
    }
    println("******** Async rpc consume ${System.currentTimeMillis() - time2} ms ********")
}