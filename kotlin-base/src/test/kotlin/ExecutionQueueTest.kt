import com.xiao.base.executor.AsyncUtil
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
}