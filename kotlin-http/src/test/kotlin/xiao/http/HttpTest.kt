package xiao.http

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import xiao.base.executor.DefaultExecutorServiceFactory
import xiao.base.testing.KtTestBase
import xiao.http.util.UrlParser
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

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
        val completable = CompletableFuture<Unit>()
        DEFAULT_SCOPE.launch {
            val completableDeferred = Http.deferred(request)
            val response = completableDeferred.awaitNanos()
            assertEquals(response.status, 200)
            assertTrue(response.asString()!!.isNotBlank())
            completable.complete(Unit)
        }
        completable.get()
    }

    companion object {
        val request = UrlParser.parseUrl("https://www.baidu.com")
        const val timeout = 5000L
        val DEFAULT_SCOPE: CoroutineScope = object : CoroutineScope {
            override val coroutineContext: CoroutineContext
                get() = DefaultExecutorServiceFactory.newExecutorService(4).asCoroutineDispatcher()
        }
    }
}