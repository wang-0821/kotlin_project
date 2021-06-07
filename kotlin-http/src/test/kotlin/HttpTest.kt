import com.xiao.base.testing.KtTestBase
import com.xiao.base.util.ThreadUtils
import com.xiao.rpc.Http
import com.xiao.rpc.util.UrlParser
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
class HttpTest : KtTestBase() {
    @Test
    fun `test http sync`() {
        val response = Http.sync(request)
        assertEquals(response.status, 200)
        assertTrue(response.asString()!!.isNotBlank())
    }

    @Test
    fun `test http future`() {
        val future = Http.async(request)
        val response = future.get(timeout, TimeUnit.MILLISECONDS)
        assertEquals(response.status, 200)
        assertTrue(response.asString()!!.isNotBlank())
    }

    @Test
    fun `test http coroutine`() {
        val job = ThreadUtils.DEFAULT_SCOPE.launch {
            val completableDeferred = Http.deferred(request)
            val response = completableDeferred.awaitNanos()
            assertEquals(response.status, 200)
            assertTrue(response.asString()!!.isNotBlank())
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