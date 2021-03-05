import com.xiao.base.executor.DefaultExecutorServiceFactory
import com.xiao.base.executor.ExecutionQueue
import com.xiao.metrics.MetricsEvent
import com.xiao.metrics.MetricsType
import com.xiao.metrics.MetricsUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.random.Random

/**
 *
 * @author lix wang
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MetricsUtilsTest {
    private lateinit var executionQueue: ExecutionQueue

    @BeforeAll
    fun setup() {
        executionQueue = ExecutionQueue(
            "MetricsTestQueue",
            DefaultExecutorServiceFactory.newExecutorService(5)
        )
    }

    @Test
    fun `test metrics latency recording`() {
        executionQueue.submit {
            for (i in 1..2000) {
                MetricsUtils.recordMetrics(
                    MetricsEvent(MetricsType.API, "MetricsTestTask"),
                    Random.nextInt(100)
                )
            }
        }.get()

        MetricsUtils.resetSummary()
        val total = MetricsUtils.metricsSummary().values.sumOf { it.times }
        Assertions.assertEquals(total, 10000)
    }
}