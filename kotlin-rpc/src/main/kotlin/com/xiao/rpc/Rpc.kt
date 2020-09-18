package com.xiao.rpc

import com.xiao.base.executor.ExecutorUtil
import com.xiao.base.executor.QueueItem
import com.xiao.base.logging.Logging
import com.xiao.rpc.io.Request
import com.xiao.rpc.util.UrlParser
import org.apache.logging.log4j.ThreadContext
import java.util.UUID
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author lix wang
 */
object Rpc: Logging() {
    val client = Client()
    var started = AtomicInteger(0)

    fun asyncCall(name: String, request: Request): Future<String?> {
        return ExecutorUtil.submit(
            object : QueueItem<String?>(name) {
                override fun execute(): String? {
                    return client.newCall(request).execute().asString()
                }

                override fun call(): String? {
                    started.getAndIncrement()
                    ThreadContext.put("RpcRequestId", UUID.randomUUID().toString())
                    val result = super.call()
                    ThreadContext.clearMap()
                    started.getAndDecrement()
                    return result
                }
            }
        )
    }
}

fun main() {
//    var total: Long = 0
//    var asStringTotal: Long = 0
//    val client = Client()
    val request = UrlParser.parseUrl("http://www.baidu.com")
//    val time1 = System.currentTimeMillis()
//    for (i in 1..100) {
//        val a = System.currentTimeMillis()
//        val response = client.newCall(request).execute()
//        val asStringStart = System.currentTimeMillis()
//        response.asString()
//        val b = System.currentTimeMillis()
//        asStringTotal += (b - asStringStart)
//        total = total + b - a
//        println("Task-$i cost ${b - a} ms")
//        println()
//    }
//    println("******** Sync rpc consume ${System.currentTimeMillis() - time1}, $total ms, asStringTotal $asStringTotal ********")


    val time2 = System.currentTimeMillis()
    val futures = mutableListOf<Future<String?>>()
    for (i in 1..100) {
        futures.add(Rpc.asyncCall("Task-$i", request))
    }
    val executor = ExecutorUtil.executor.executorService as ThreadPoolExecutor
    Thread {
        while (true) {
            println("Queued size ${executor.queue.size}, active size ${executor.activeCount}, completed ${executor.completedTaskCount}, start: ${Rpc.started.get()}")
            if (executor.activeCount <= 0) {
                break
            }
            Thread.sleep(60)
        }
    }.start()

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