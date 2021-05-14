package lock

import com.xiao.base.executor.DefaultExecutorServiceFactory
import com.xiao.base.executor.ExecutionQueue
import com.xiao.base.lock.AtomicLock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 *
 * @author lix wang
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AtomicLockTest {
    @Test
    fun `test atomic lock`() {
        val lock = AtomicLock()
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
    }
}