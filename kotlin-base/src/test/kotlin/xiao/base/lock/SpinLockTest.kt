package xiao.base.lock

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import xiao.base.executor.DefaultExecutorServiceFactory
import xiao.base.executor.ExecutionQueue
import xiao.base.testing.KtTestBase

/**
 *
 * @author lix wang
 */
class SpinLockTest : KtTestBase() {
    @Test
    fun `test atomic lock`() {
        val lock = SpinLock()
        val result = mutableListOf<Int>()
        val executionQueue = ExecutionQueue(
            "atomic-xiao.base.lock-test-exec",
            DefaultExecutorServiceFactory.newExecutorService(3)
        )

        executionQueue.submit {
            lock.use {
                Thread.sleep(500)
                result.add(1)
            }
        }

        executionQueue.submit {
            lock.use {
                result.add(2)
            }
        }

        Thread.sleep(1000)
        Assertions.assertEquals(result[0], 1)
        Assertions.assertEquals(result[1], 2)
    }
}