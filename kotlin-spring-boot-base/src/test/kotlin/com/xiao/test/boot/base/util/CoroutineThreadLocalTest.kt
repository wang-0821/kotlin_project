package com.xiao.test.boot.base.util

import com.xiao.base.thread.CoroutineThreadLocal
import com.xiao.base.thread.KtFastThreadLocal
import com.xiao.boot.base.thread.KtThreadPool
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

/**
 * @author lix wang
 */
class CoroutineThreadLocalTest {
    @Test
    fun `test coroutine thread local`() {
        val value = "hello world"
        val threadLocal = KtFastThreadLocal<String>()
        val coroutineThreadLocal = CoroutineThreadLocal(threadLocal, value)
        val completable = CompletableFuture<Unit>()
        KtThreadPool.globalCoroutineScope.launch(coroutineThreadLocal) {
            Assertions.assertEquals(threadLocal.get(), value)
            runBlocking(KtThreadPool.globalCoroutineScope.coroutineContext + coroutineThreadLocal) {
                Assertions.assertEquals(threadLocal.get(), value)
                threadLocal.set("123")
            }
            yield()
            Assertions.assertEquals(threadLocal.get(), value)
            completable.complete(Unit)
        }
        completable.get()
    }
}