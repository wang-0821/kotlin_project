package thread

import com.xiao.base.testing.KtTestBase
import com.xiao.base.thread.KtFastThreadLocal
import com.xiao.base.thread.KtThread
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author lix wang
 */
class KtFastThreadLocalTest : KtTestBase() {
    @Test
    fun `test custom fast thread local usage`() {
        val fastThreadLocal = KtFastThreadLocal<AtomicInteger>()
        fastThreadLocal.fetch {
            AtomicInteger(1)
        }
        val completableFuture = CompletableFuture<Unit>()
        KtThread {
            fastThreadLocal.fetch {
                AtomicInteger(2)
            }
            completableFuture.complete(Unit)
        }.start()

        completableFuture.get()
        Assertions.assertEquals(fastThreadLocal.get()!!.get(), 1)
    }

    @Test
    fun `test custom fast thread local with concurrency`() {
        val fastThreadLocal = KtFastThreadLocal<AtomicInteger>()
        val globalAtomicInt = AtomicInteger(0)
        val completableFuture1 = CompletableFuture<AtomicInteger>()
        KtThread {
            fastThreadLocal.fetch {
                AtomicInteger(1)
            }
            globalAtomicInt.set(2)
            completableFuture1.complete(fastThreadLocal.get())
        }.start()

        val completableFuture2 = CompletableFuture<AtomicInteger>()
        KtThread {
            Thread.sleep(500)
            globalAtomicInt.set(1)
            fastThreadLocal.fetch {
                AtomicInteger(2)
            }
            completableFuture2.complete(fastThreadLocal.get())
        }.start()

        Assertions.assertEquals(completableFuture1.get().get(), 1)
        Assertions.assertEquals(completableFuture2.get().get(), 2)
        Assertions.assertEquals(globalAtomicInt.get(), 1)
    }
}