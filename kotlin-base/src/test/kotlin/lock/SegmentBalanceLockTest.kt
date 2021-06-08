package lock

import com.xiao.base.executor.DefaultExecutorServiceFactory
import com.xiao.base.executor.ExecutionQueue
import com.xiao.base.lock.SegmentBalanceLock
import com.xiao.base.testing.KtTestBase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 *
 * @author lix wang
 */
class SegmentBalanceLockTest : KtTestBase() {
    @Test
    fun `test segment balance lock`() {
        val executionQueue = ExecutionQueue(
            "segment-balance-lock-exec",
            DefaultExecutorServiceFactory.newExecutorService(10)
        )
        val segmentLock = SegmentBalanceLock(4)
        val summary = IntArray(4)
        val futures = (0..9999).map { _ ->
            executionQueue.submit {
                segmentLock.use {
                    summary[it]++
                }
            }
        }

        futures.forEach {
            it.get()
        }

        println(summary.joinToString())
        // assert lock is spread to each segment.
        Assertions.assertTrue(summary.all { it > 2300 }) {
            summary.joinToString()
        }
    }
}