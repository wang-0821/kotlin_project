package com.xiao.base.demo.coroutines

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

/**
 *
 * @author lix wang
 */
class ShareMutableState {
    suspend fun massiveRun(action: suspend () -> Unit) {
        val n = 100
        val k = 1000
        val time = measureTimeMillis {
            coroutineScope {
                repeat(n) {
                    launch {
                        repeat(k) {
                            action()
                        }
                    }
                }
            }
        }
        println("Completed ${n * k} actions in $time ms")
    }

    fun atomicCounter() = runBlocking {
        val counter = AtomicInteger()
        withContext(Dispatchers.Default) {
            massiveRun {
                counter.incrementAndGet()
            }
        }
        println("Counter = $counter")
    }

    fun fineGrainedCounter() = runBlocking {
        var counter = 0
        val counterContext = newSingleThreadContext("CounterContext")
        withContext(Dispatchers.Default) {
            massiveRun {
                withContext(counterContext) {
                    counter++
                }
            }
        }
        println("Counter = $counter")
    }

    fun coarseGrainedCounter() = runBlocking {
        var counter = 0
        val counterContext = newSingleThreadContext("CounterContext")
        withContext(counterContext) {
            massiveRun {
                counter++
            }
        }
        println("Counter = $counter")
    }

    fun mutualExcludeCounter() = runBlocking {
        val mutex = Mutex()
        var counter = 0
        withContext(Dispatchers.Default) {
            massiveRun {
                mutex.withLock {
                    counter++
                }
            }
        }
        println("Counter = $counter")
    }

    private fun CoroutineScope.actorCounter() = actor<CounterMsg> {
        var counter = 0
        for (msg in channel) {
            when (msg) {
                is IncCounter -> counter++
                is GetCounter -> msg.response.complete(counter)
            }
        }
    }

    fun actorCounterRun() = runBlocking {
        val counter = actorCounter()
        withContext(Dispatchers.Default) {
            massiveRun {
                counter.send(IncCounter)
            }
        }
        val response = CompletableDeferred<Int>()
        counter.send(GetCounter(response))
        println("Counter = ${response.await()}")
        counter.close()
    }

    fun CoroutineScope.fizz() = produce {
        while (true) {
            delay(300)
            send("Fizz")
        }
    }

    fun CoroutineScope.buzz() = produce {
        while (true) {
            delay(500)
            send("Buzz")
        }
    }

    suspend fun selectFizzBuzz() = runBlocking {
        val fizz = fizz()
        val buzz = buzz()
        select<Unit> {
            fizz.onReceive { value ->
                println(" fizz -> '$value'")
            }
            buzz.onReceive { value ->
                println("buzz -> $value")
            }
        }
    }
}

fun main() {
    val obj = ShareMutableState()
    obj.fineGrainedCounter()
    obj.atomicCounter()
    obj.coarseGrainedCounter()
    obj.mutualExcludeCounter()
    obj.actorCounterRun()
    runBlocking {
        repeat(7) {
            obj.selectFizzBuzz()
        }
        coroutineContext.cancelChildren()
    }
}
