package com.xiao.rpc

import com.xiao.base.executor.ExecutorUtil
import com.xiao.base.executor.QueueItem
import com.xiao.base.logging.Logging
import com.xiao.rpc.io.Request
import com.xiao.rpc.util.UrlParser
import org.apache.logging.log4j.ThreadContext
import java.util.UUID
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
object Rpc: Logging() {
    val client = Client()

    fun asyncCall(name: String, request: Request): Future<String?> {
        return ExecutorUtil.submit(
            object : QueueItem<String?>(name) {
                override fun execute(): String? {
                    return client.newCall(request).execute().asString()
                }

                override fun call(): String? {
                    ThreadContext.put("RpcRequestId", UUID.randomUUID().toString())
                    val result = super.call()
                    ThreadContext.clearMap()
                    return result
                }
            }
        )
    }
}

fun main() {
//    var total: Long = 0
//    val client = Client()
    val request = UrlParser.parseUrl("http://www.baidu.com")
//    val time1 = System.currentTimeMillis()
//    for (i in 1..100) {
//        val a = System.currentTimeMillis()
//        client.newCall(request).execute().contentAsString()
//        val b = System.currentTimeMillis()
//        total = total + b - a
//        println("Task-$i cost ${b - a} ms")
//        println()
//    }
//    println("******** Sync rpc consume ${System.currentTimeMillis() - time1}, $total ms ********")


    val time2 = System.currentTimeMillis()
    val futures = mutableListOf<Future<String?>>()
    for (i in 1..50) {
        futures.add(Rpc.asyncCall("Task-$i", request))
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