package com.xiao.metrics

import com.xiao.base.executor.DefaultExecutorServiceFactory
import com.xiao.base.testing.KtTestBase
import com.xiao.base.thread.KtThread
import com.xiao.metrics.handler.MetricsCountHandler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author lix wang
 */
class MetricsTest : KtTestBase() {
    @Test
    fun `test add latency`() {
        val completableFuture = CompletableFuture<Unit>()
        KtThread {
            try {
                MetricsUtils.recordMetrics(
                    MetricsUtils.TYPE_API,
                    MetricsUtils.STATE_SUCCESS,
                    10,
                    this::class.java.simpleName,
                    "testAddLatency"
                )
            } finally {
                completableFuture.complete(Unit)
            }
        }.start()
        completableFuture.get()
        Assertions.assertTrue {
            MetricsUtils.metricsSummary().isEmpty()
        }
        MetricsUtils.updateSummary()
        val summary = MetricsUtils.metricsSummary()
            .filter { (event, _) ->
                event.type == MetricsUtils.TYPE_API
            }.values.first()
        Assertions.assertEquals(summary.total, 1)
        Assertions.assertEquals(summary.avg, 10)
    }

    @Test
    fun `test metrics scheduler`() {
        val counter = AtomicInteger()
        val metricsScheduler = MetricsScheduler(
            "metrics-scheduler",
            DefaultExecutorServiceFactory,
            listOf(MetricsCountHandler(counter))
        )

        val completableFuture1 = CompletableFuture<Unit>()
        val completableFuture2 = CompletableFuture<Unit>()
        KtThread {
            try {
                MetricsUtils.recordMetrics(
                    MetricsUtils.TYPE_DB,
                    MetricsUtils.STATE_SUCCESS,
                    10,
                    this::class.java.simpleName,
                    "testDBLatency"
                )
            } finally {
                completableFuture1.complete(Unit)
            }
        }.start()
        KtThread {
            try {
                MetricsUtils.recordMetrics(
                    MetricsUtils.TYPE_RPC,
                    MetricsUtils.STATE_SUCCESS,
                    10,
                    this::class.java.simpleName,
                    "testRPCLatency"
                )
            } finally {
                completableFuture2.complete(Unit)
            }
        }.start()

        completableFuture1.get()
        completableFuture2.get()

        metricsScheduler.executeMetrics()
        Assertions.assertEquals(counter.get(), 2)
    }
}