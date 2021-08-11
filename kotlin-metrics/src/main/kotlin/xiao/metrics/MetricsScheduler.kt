package xiao.metrics

import xiao.base.executor.ExecutorServiceFactory
import xiao.base.scheduler.CronScheduler
import xiao.base.scheduler.ScheduledTask
import xiao.metrics.handler.MetricsHandler
import java.util.concurrent.TimeUnit

/**
 *
 * @author lix wang
 */
class MetricsScheduler(
    name: String,
    executorServiceFactory: ExecutorServiceFactory,
    private val metricsHandlers: List<MetricsHandler>
) : CronScheduler(
    name,
    executorServiceFactory.newScheduledExecutorService(name, 1)
) {
    @ScheduledTask(initial = 5, fixedDelay = 30, timeUnit = TimeUnit.SECONDS)
    fun executeMetrics() {
        val oldSummary = MetricsUtils.metricsSummary()
        MetricsUtils.updateSummary()
        val newSummary = MetricsUtils.metricsSummary()

        metricsHandlers.forEach {
            it.handle(oldSummary, newSummary)
        }
    }
}