package lock

import com.xiao.base.executor.DefaultExecutorServiceFactory
import com.xiao.base.executor.ExecutionQueue
import com.xiao.base.lock.SpinLock
import com.xiao.base.testing.KtTestBase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

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
            "atomic-lock-test-exec",
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