package xiao.redis.scheduler

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xiao.base.executor.DefaultExecutorServiceFactory
import xiao.base.scheduler.AbstractScheduler
import xiao.base.testing.KtTestBase
import xiao.base.util.ThreadUtils
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author lix wang
 */
class BaseSchedulerTest : KtTestBase() {
    private lateinit var scheduler: AbstractScheduler

    @BeforeEach
    fun setup() {
        val scheduledExecutorService = DefaultExecutorServiceFactory.newScheduledExecutorService(2)
        scheduler = object : AbstractScheduler("TestScheduledExecutor", scheduledExecutorService) {}
    }

    @Test
    fun `test schedule task`() {
        val value = AtomicInteger(0)
        val scheduledFuture = scheduler.schedule(Duration.ZERO) {
            value.set(2)
        }

        while (true) {
            if (scheduledFuture.isDone) {
                break
            }
        }

        Assertions.assertEquals(value.get(), 2)
    }

    @Test
    fun `test schedule task cancel`() {
        val value = AtomicInteger(0)
        val scheduledFuture = scheduler.scheduleAtFixedRate(Duration.ZERO, Duration.ofMillis(100)) {
            value.getAndIncrement()
        }
        ThreadUtils.safeSleep(50)
        scheduledFuture.cancel(false)

        ThreadUtils.safeSleep(100)
        val currentValue = value.get()
        ThreadUtils.safeSleep(100)
        Assertions.assertEquals(currentValue, value.get())
        Assertions.assertTrue(currentValue > 0)
    }
}