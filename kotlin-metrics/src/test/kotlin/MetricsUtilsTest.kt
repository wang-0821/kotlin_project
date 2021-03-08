import com.xiao.base.executor.DefaultExecutorServiceFactory
import com.xiao.base.executor.ExecutionQueue
import com.xiao.metrics.MetricsEvent
import com.xiao.metrics.MetricsType
import com.xiao.metrics.MetricsUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random

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
        val metricsFuture = executionQueue.submit {
            for (i in 1..1000) {
                MetricsUtils.recordMetrics(
                    MetricsEvent(MetricsType.API, "MetricsApiRecording"),
                    Random.nextInt(100)
                )

                MetricsUtils.recordMetrics(
                    MetricsEvent(MetricsType.DB, "MetricsDbRecording"),
                    Random.nextInt(10)
                )
            }
        }

        metricsFuture.get()

        MetricsUtils.resetSummary()
        val total = MetricsUtils.metricsSummary().values.sumOf { it.times }
        Assertions.assertEquals(total, 2000)
    }

    @Test
    fun `test multi thread metrics latency recording`() {
        val metricsFutures = (0..4).map {
            executionQueue.submit {
                for (i in 1..500) {
                    MetricsUtils.recordMetrics(
                        MetricsEvent(MetricsType.API, "MetricsApiRecording"),
                        Random.nextInt(100)
                    )

                    MetricsUtils.recordMetrics(
                        MetricsEvent(MetricsType.DB, "MetricsDbRecording"),
                        Random.nextInt(10)
                    )

                    MetricsUtils.recordMetrics(
                        MetricsEvent(MetricsType.RPC, "MetricsRpcRecording"),
                        Random.nextInt(20)
                    )
                }
            }
        }

        metricsFutures.forEach {
            it.get()
        }

        MetricsUtils.resetSummary()
        val total = MetricsUtils.metricsSummary().values.sumOf { it.times }
        Assertions.assertEquals(total, 7500)
    }
}