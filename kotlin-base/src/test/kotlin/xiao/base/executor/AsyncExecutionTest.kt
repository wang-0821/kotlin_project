package xiao.base.executor

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xiao.base.testing.KtTestBase
import xiao.base.util.ThreadUtils
import xiao.base.util.deferredSuspend
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author lix wang
 */
class AsyncExecutionTest : KtTestBase() {
    private lateinit var executionQueue: ExecutionQueue

    private lateinit var coroutineScope: CoroutineScope

    @BeforeEach
    fun setup() {
        executionQueue = ExecutionQueue(
            "Execution-queue-test",
            DefaultExecutorServiceFactory.newExecutorService(8)
        )
        coroutineScope = object : CoroutineScope {
            override val coroutineContext: CoroutineContext
                get() = DefaultExecutorServiceFactory.newExecutorService(8).asCoroutineDispatcher()
        }
    }

    @Test
    fun `test submit runnable`() {
        val map = mutableMapOf<String, String>()
        executionQueue.submit {
            Thread.sleep(1000L)
            map["Hello"] = "world!"
        }
        Assertions.assertEquals(map["Hello"], null)
        Thread.sleep(2000L)
        Assertions.assertEquals(map["Hello"], "world!")
    }

    @Test
    fun `test submit callable`() {
        val future = executionQueue.submit {
            return@submit 100
        }
        Assertions.assertEquals(future.get(), 100)
    }

    @Test
    fun `test execution queue async task cancel while mayInterruptIfRunning is false`() {
        val i = AtomicInteger(0)
        val future = executionQueue.submit("endless loop") {
            while (true) {
                if (!Thread.currentThread().isInterrupted) {
                    i.getAndIncrement()
                } else {
                    break
                }
                Thread.sleep(100)
            }
        }
        Assertions.assertEquals(executionQueue.taskCount(), 1)
        Assertions.assertFalse(future.isDone)

        // cancel task
        ThreadUtils.safeSleep(500)
        future.cancel(false)
        val value = i.get()

        Assertions.assertEquals(executionQueue.taskCount(), 0)
        Assertions.assertTrue(future.isDone)

        // This means task is not been cancelled actually.
        ThreadUtils.safeSleep(500)
        Assertions.assertTrue(i.get() > value && value > 0)
    }

    @Test
    fun `test execution queue async task cancel while mayInterruptIfRunning is true`() {
        val i = AtomicInteger(0)
        val future = executionQueue.submit("endless loop") {
            while (true) {
                if (!Thread.currentThread().isInterrupted) {
                    i.getAndIncrement()
                } else {
                    break
                }
                Thread.sleep(100)
            }
        }
        Assertions.assertEquals(executionQueue.taskCount(), 1)
        Assertions.assertFalse(future.isDone)

        ThreadUtils.safeSleep(500)
        future.cancel(true)
        val value = i.get()

        Assertions.assertTrue(future.isDone)

        ThreadUtils.safeSleep(500)
        Assertions.assertTrue(value == i.get() && value > 0)
        Assertions.assertEquals(executionQueue.taskCount(), 0)
    }

    @Test
    fun `test execution queue async task run with exception`() {
        val value = AtomicInteger(0)
        val future = executionQueue.submit {
            throw IllegalStateException()
        }
        future.whenComplete { _, throwable ->
            throwable?.let {
                value.set(2)
            }
        }
        ThreadUtils.safeSleep(500)
        Assertions.assertEquals(value.get(), 2)
    }

    @Test
    fun `test coroutine async task`() {
        val job = coroutineScope.launch {
            val list = mutableListOf<Int>()
            val safeDeferred = deferredSuspend {
                async {
                    delay(300)
                    list.add(1)
                }
                async {
                    list.add(2)
                }
                list.add(3)
                delay(500)
                list
            }
            Assertions.assertEquals(listOf(3, 2, 1), safeDeferred.awaitNanos())
        }
        while (true) {
            if (job.isCompleted) {
                break
            }
        }
    }

    @Test
    fun `test coroutine async task cancel`() {
        val value = AtomicInteger(0)
        val deferredReference = AtomicReference<SafeDeferred<*>>()
        coroutineScope.launch {
            val d = deferredSuspend {
                try {
                    while (true) {
                        value.getAndIncrement()
                        delay(100)
                    }
                } catch (e: CancellationException) {
                    return@deferredSuspend
                }
            }
            deferredReference.set(d)
        }

        ThreadUtils.safeSleep(1000)
        deferredReference.get().cancel()
        val currentValue = value.get()

        ThreadUtils.safeSleep(500)
        Assertions.assertTrue(deferredReference.get().isCompleted)
        Assertions.assertTrue(deferredReference.get().isCanceled)
        Assertions.assertTrue(currentValue == value.get())
    }
}