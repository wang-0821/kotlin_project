import com.xiao.base.executor.AsyncUtil
import com.xiao.base.util.awaitNanos
import com.xiao.base.util.deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.Callable

/**
 *
 * @author lix wang
 */
class ExecutionQueueTest {
    @Test
    fun `test submit runnable`() {
        val map = mutableMapOf<String, String>()
        AsyncUtil.executor.submit {
            Thread.sleep(1000L)
            map["Hello"] = "world!"
        }
        Assertions.assertEquals(map["Hello"], null)
        Thread.sleep(2000L)
        Assertions.assertEquals(map["Hello"], "world!")
    }

    @Test
    fun `test submit callable`() {
        val future = AsyncUtil.executor.submit(
            Callable {
                return@Callable 100
            }
        )
        Assertions.assertEquals(future.get(), 100)
    }

    @Test
    fun `test coroutine scope`() {
        val job = AsyncUtil.coroutineScope.launch {
            val list = mutableListOf<Int>()
            val completableDeferred = deferred {
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
            Assertions.assertEquals(listOf(3, 2, 1), completableDeferred.awaitNanos())
        }
        while (true) {
            if (job.isCompleted) {
                break
            }
        }
    }
}