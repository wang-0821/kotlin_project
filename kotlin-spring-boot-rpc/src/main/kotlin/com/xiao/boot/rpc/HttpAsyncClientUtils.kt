package com.xiao.boot.rpc

import com.xiao.base.thread.NamedThreadFactory
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder
import org.apache.hc.core5.reactor.IOReactorConfig
import org.apache.hc.core5.util.TimeValue
import java.util.concurrent.TimeUnit
import kotlin.math.min

val asyncConnectionManager: PoolingAsyncClientConnectionManager = PoolingAsyncClientConnectionManagerBuilder.create()
    .setMaxConnTotal(2000)
    .setMaxConnPerRoute(300)
    .build()

val asyncIOReactorConfig: IOReactorConfig = IOReactorConfig.custom()
    .setIoThreadCount(min(4, Runtime.getRuntime().availableProcessors()))
    .setSoTimeout(200, TimeUnit.MILLISECONDS)
    .setSoKeepAlive(true)
    .build()

val asyncHttpClient: CloseableHttpAsyncClient by lazy {
    HttpAsyncClients.custom()
        .setConnectionManager(asyncConnectionManager)
        .setIOReactorConfig(asyncIOReactorConfig)
        .setThreadFactory(NamedThreadFactory("http-nio"))
        .evictIdleConnections(TimeValue.ofSeconds(60L))
        .disableConnectionState()
        .disableAutomaticRetries()
        .disableCookieManagement()
        .disableAuthCaching()
        .build()
        .apply {
            start()
        }
}
