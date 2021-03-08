import com.xiao.base.executor.DefaultExecutorServiceFactory
import com.xiao.base.executor.ExecutionQueue
import com.xiao.base.util.ThreadUtils
import com.xiao.metrics.MetricsEvent
import com.xiao.metrics.MetricsScheduler
import com.xiao.metrics.MetricsType
import com.xiao.metrics.MetricsUtils
import com.xiao.metrics.handler.ApiSlowMetricsHandler
import com.xiao.metrics.handler.MetricsCalculationHandler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

/**
 *
 * @author lix wang
 */
class MetricsSchedulerTest {
    private lateinit var executionQueue: ExecutionQueue

    @BeforeEach
    fun setup() {
        executionQueue = ExecutionQueue(
            "MetricsExecutionQueue",
            DefaultExecutorServiceFactory.newExecutorService(5)
        )
    }

    @Test
    fun `test metrics scheduler`() {
        val start = System.currentTimeMillis()
        val maxTime = AtomicLong(0L)
        val metricsFutures = (1..5).map {
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
            it.whenComplete { _, _ ->
                val total = System.currentTimeMillis() - start
                if (total > maxTime.get()) {
                    maxTime.set(total)
                }
            }
        }

        val counter = AtomicInteger(0)
        val metricsScheduler = MetricsScheduler(
            "MetricsScheduler",
            DefaultExecutorServiceFactory,
            listOf(ApiSlowMetricsHandler(), MetricsCalculationHandler(counter))
        )
        metricsScheduler.start()

        // wait metrics scheduler complete
        ThreadUtils.safeSleep(6000)
        metricsScheduler.shutdown()

        Assertions.assertEquals(counter.get(), 7500)
    }
}