import com.xiao.base.util.ThreadUtils
import com.xiao.rpc.Rpc
import com.xiao.rpc.util.UrlParser
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
class RpcTest {
    @Test
    fun `test rpc sync`() {
        val response = Rpc.sync(request)
        assertEquals(response.status, 200)
        assertFalse(response.asString().isNullOrBlank())
    }

    @Test
    fun `test rpc future`() {
        val future = Rpc.async(request)
        val response = future.get(timeout, TimeUnit.MILLISECONDS)
        assertEquals(response.status, 200)
        assertFalse(response.asString().isNullOrBlank())
    }

    @Test
    fun `test rpc coroutine`() {
        val job = ThreadUtils.DEFAULT_SCOPE.launch {
            val completableDeferred = Rpc.deferred(request)
            val result = completableDeferred.awaitNanos()
            assertEquals(result.status, 200)
            assertFalse(result.asString().isNullOrBlank())
        }
        while (true) {
            if (job.isCompleted) {
                break
            }
        }
    }

    companion object {
        val request = UrlParser.parseUrl("https://www.baidu.com")
        const val timeout = 5000L
    }
}