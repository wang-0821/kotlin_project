import com.xiao.base.executor.ExecutionQueue
import com.xiao.base.util.ThreadUtils
import com.xiao.base.util.deferredSuspend
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 *
 * @author lix wang
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExecutionQueueTest {
    lateinit var executionQueue: ExecutionQueue

    @BeforeAll
    fun setup() {
        executionQueue = ExecutionQueue("Execution-queue-test", ThreadUtils.DEFAULT_EXECUTOR)
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
    fun `test coroutine scope`() {
        val job = ThreadUtils.coroutineScope.launch {
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
}