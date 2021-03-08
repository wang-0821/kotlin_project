import com.xiao.base.executor.DefaultExecutorServiceFactory
import com.xiao.base.executor.ExecutionQueue
import com.xiao.metrics.MetricsEvent
import com.xiao.metrics.MetricsType
import com.xiao.metrics.MetricsUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author lix wang
 */
class MetricsPerfTest {
    private lateinit var executionQueue: ExecutionQueue

    @BeforeEach
    fun setup() {
        executionQueue = ExecutionQueue(
            "MetricsExecutionQueue",
            DefaultExecutorServiceFactory.newExecutorService(5)
        )
    }

    @Test
    fun `test 10000 metrics perf with a thread`() {
        val start = System.currentTimeMillis()
        (1..10000).forEach { _ ->
            MetricsUtils.recordMetrics(
                MetricsEvent(MetricsType.API, "MetricsPerfTest"),
                100
            )
        }

        val qps = 10000 * 1000 / (System.currentTimeMillis() - start)
        Assertions.assertTrue(qps > 80000)
    }

    @Test
    fun `test 50000 metrics perf with a thread`() {
        val start = System.currentTimeMillis()
        (1..50000).forEach { _ ->
            MetricsUtils.recordMetrics(
                MetricsEvent(MetricsType.API, "MetricsPerfTest"),
                100
            )
        }

        val qps = 10000 * 1000 / (System.currentTimeMillis() - start)
        Assertions.assertTrue(qps > 80000)
    }

    @Test
    fun `test 2 types metrics with multi threads`() {
        val start = System.currentTimeMillis()
        val apiMetricsFuture = executionQueue.submit {
            (1..50000).forEach { _ ->
                MetricsUtils.recordMetrics(
                    MetricsEvent(MetricsType.API, "MetricsApiPerfTest"),
                    100
                )
            }
        }
        val dbMetricsFuture = executionQueue.submit {
            (1..50000).forEach { _ ->
                MetricsUtils.recordMetrics(
                    MetricsEvent(MetricsType.DB, "MetricsDbPerfTest"),
                    5
                )
            }
        }

        apiMetricsFuture.get()
        dbMetricsFuture.get()

        val qps = 10000 * 1000 / (System.currentTimeMillis() - start)
        Assertions.assertTrue(qps > 80000)
    }
}