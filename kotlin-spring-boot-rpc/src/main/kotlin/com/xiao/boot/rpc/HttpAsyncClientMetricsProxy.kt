package com.xiao.boot.rpc

import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import java.lang.reflect.Method

/**
 * monitor http exchange metrics.
 */
class HttpAsyncClientMetricsProxy<out T>(
    target: CloseableHttpAsyncClient, clazz: Class<T>
): AbstractInvocationHandler<T>(target, clazz) {
    override fun doInvoke(proxy: Any, method: Method, args: Array<Any?>?): Any? {
        TODO("Not yet implemented")
    }
}