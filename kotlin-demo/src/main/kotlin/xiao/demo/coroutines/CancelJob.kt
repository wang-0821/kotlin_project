package xiao.demo.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 *
 * @author lix wang
 */
class CancelJob {
    fun cancelJob() = runBlocking(Dispatchers.Unconfined) {
        val job = launch {
            repeat(1000) {
                println("job: I'm sleeping $it...")
                delay(500L)
            }
        }
        delay(1300L)
        println("main: I'm tired of waiting!")
        job.cancel()
        job.join()
        println("main: Now I can quit.")
    }

    fun notCancelJob() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (i < 5) {
                // 每秒打印两次消息
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++}...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }

    fun cancelWhileComputing() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextTime = startTime
            var i = 0
            while (isActive) {
                if (System.currentTimeMillis() >= nextTime) {
                    println("job: I'm sleeping ${i++}...")
                    nextTime += 500L
                }
            }
        }
        delay(1300L)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I an quit.")
    }

    fun cancelUseFinally() = runBlocking {
        val job = launch {
            try {
                repeat(1000) {
                    println("job: I'm sleeping $it...")
                    delay(500L)
                }
            } finally {
                withContext(NonCancellable) {
                    println("job: I'm running finally")
                    delay(1000L)
                    println("job: Anf I've just delayed for 1 sec because I'm non-cancellable")
                }
                // 加了这个delay下面不会再继续执行，因为delay是挂起函数，会检查结束状态，从而不会继续往下执行，会直接cancel接下来的操作。
                delay(200L)
                println("job: I'm running finally2")
            }
        }
        delay(1300L)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }

    fun timeoutCancel() = runBlocking {
        val result = withTimeoutOrNull(1300L) {
            repeat(1000) {
                println("I'm sleeping $it...")
                delay(500L)
            }
            "Done"
        }
        println("Result is $result")
    }
}

fun main() {
    val obj = CancelJob()
//    cancelJob()
//    notCancelJob()
//    cancelWhileComputing()
//    cancelUseFinally()
    obj.timeoutCancel()
}