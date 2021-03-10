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
class MetricsUtilsTest {
    private lateinit var executionQueue: ExecutionQueue

    @BeforeEach
    fun setup() {
        executionQueue = ExecutionQueue(
            "MetricsTestQueue",
            DefaultExecutorServiceFactory.newExecutorService(5)
        )
    }

    @Test
    fun `test metrics latency recording`() {
        for (i in 1..1000) {
            MetricsUtils.recordMetrics(
                MetricsEvent(MetricsType.API, "MetricsApiRecording"),
                i
            )

            MetricsUtils.recordMetrics(
                MetricsEvent(MetricsType.DB, "MetricsDbRecording"),
                i
            )
        }

        MetricsUtils.resetSummary()
        val total = MetricsUtils.metricsSummary().values.sumBy { it.times }
        Assertions.assertEquals(total, 2000)
    }

    @Test
    fun `test multi thread metrics latency recording`() {
        val metricsFutures = (0..4).map {
            val start = 500 * it
            executionQueue.submit {
                for (i in start + 1..start + 500) {
                    MetricsUtils.recordMetrics(
                        MetricsEvent(MetricsType.API, "MetricsApiRecording"),
                        i
                    )

                    MetricsUtils.recordMetrics(
                        MetricsEvent(MetricsType.DB, "MetricsDbRecording"),
                        i
                    )

                    MetricsUtils.recordMetrics(
                        MetricsEvent(MetricsType.RPC, "MetricsRpcRecording"),
                        i
                    )
                }
            }
        }

        metricsFutures.forEach {
            it.get()
        }

        MetricsUtils.resetSummary()
        val total = MetricsUtils.metricsSummary().values.sumBy { it.times }
        Assertions.assertEquals(total, 7500)
    }

    @Test
    fun `set metrics multi threads`() {
        MetricsUtils.recordMetrics(
            MetricsEvent(MetricsType.API, "MetricsApiRecording"),
            1
        )
        MetricsUtils.recordMetrics(
            MetricsEvent(MetricsType.API, "MetricsApiRecording"),
            2
        )

        MetricsUtils.resetSummary()
        val total = MetricsUtils.metricsSummary().values.sumBy { it.times }
        Assertions.assertEquals(total, 2)
    }
}