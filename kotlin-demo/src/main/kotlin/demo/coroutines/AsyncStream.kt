package demo.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.system.measureTimeMillis

/**
 *
 * @author lix wang
 */
class AsyncStream {
    fun foo(): List<Int> = listOf(1, 2, 3)

    fun fooSequence(): Sequence<Int> = sequence {
        for (i in 1..3) {
            Thread.sleep(100)
            yield(i)
        }
    }

    suspend fun suspendFoo(): List<Int> {
        delay(1000)
        return listOf(1, 2, 3)
    }

    fun flowFoo(): Flow<Int> = flow {
        for (i in 1..3) {
            delay(100)
            emit(i) // 发送下一个值
        }
    }

    fun timeoutFlowFoo(): Flow<Int> = flow {
        for (i in 1..3) {
            delay(100)
            println("Emitting $i")
            emit(i)
        }
    }
}

fun main() {
    val obj = AsyncStream()
    println("sync consume ${measureTimeMillis { obj.foo().forEach { value -> println(value) } }} ms")
    println("async consume: ${measureTimeMillis { obj.fooSequence().forEach { value -> println(value) } }} ms")
    runBlocking { println("suspend consume ${measureTimeMillis { obj.suspendFoo().forEach { value -> println(value) } }} ms") }

    runBlocking {
        // 启动并发的协程，此时主线程并没有阻塞。
        launch {
            for (i in 1..3) {
                println("I am not blocked $i")
                delay(100)
            }
        }
        // 收集这个流
        obj.flowFoo().collect { value -> println(value) }
    }

    runBlocking {
        withTimeoutOrNull(250) {
            obj.timeoutFlowFoo().collect{ value -> println(value) }
        }
        println("Done")
    }

    runBlocking {
        listOf(1, 2, 3).asFlow().transform { request ->
            emit("Marking request $request")
        }.collect { response -> println(response) }
    }

    runBlocking {
        flow {
            try {
                emit(1)
                emit(2)
                println("This line will not execute")
                emit(3)
            } finally {
                println("Finally in numbers")
            }
        }.take(2).collect { value -> println(value) }
    }

    runBlocking {
        val total = (1..5).asFlow().map { it * it }.reduce { accumulator, value -> accumulator + value }
        println(total)
    }

    runBlocking {
        (1..5).asFlow().filter {
            println("Filter $it")
            it % 2 == 0
        }.map {
            println("Map $it")
            "string $it"
        }.collect {
            println("Collect $it")
        }
    }

    runBlocking {
        flow {
            for (i in 1..3) {
                Thread.sleep(100)
                println("Emitting $i")
                emit(i)
            }
        }.flowOn(Dispatchers.Default).collect {value ->
            println("Collected $value")
        }
    }

    runBlocking {
        val time = measureTimeMillis {
            flow {
                for (i in 1..3) {
                    delay(100)
                    emit(i)
                }
            }.buffer().collectLatest {value ->
                delay(300)
                println(value)
            }
        }
        println("Collected in $time ms")
    }

    runBlocking {
        (1..3).asFlow().onEach { delay(300) }
                .zip(flowOf("one", "two", "three").onEach { delay(400) }) { a, b -> "$a -> $b" }
                .collect{ println(it) }
    }
}