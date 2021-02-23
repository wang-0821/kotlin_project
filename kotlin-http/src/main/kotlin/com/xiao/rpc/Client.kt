package com.xiao.rpc

import com.xiao.base.annotation.AnnotatedKtResource
import com.xiao.base.context.ContextScanner
import com.xiao.base.util.packageName
import com.xiao.rpc.context.ClientContextPool
import com.xiao.rpc.handler.Chain
import com.xiao.rpc.io.Exchange
import com.xiao.rpc.io.Request
import com.xiao.rpc.io.Response

/**
 *
 * @author lix wang
 */
class Client {
    var connectTimeout = DEFAULT_TIMEOUT
    var readTimeout = DEFAULT_TIMEOUT
    var writeTimeout = DEFAULT_TIMEOUT
    var clientContextPool: ClientContextPool? = null

    init {
        refreshContext()
    }

    class Call(private val client: Client, private val request: Request, private val builder: Builder?) {
        fun execute(): Response {
            checkRequest(request)
            val chain = Chain(client, request)
            configCall(chain.exchange)
            return chain.execute()
        }

        private fun configCall(exchange: Exchange) {
            exchange.connectTimeout = builder?.connectTimeout ?: client.connectTimeout
            exchange.readTimeout = builder?.readTimeout ?: client.readTimeout
            exchange.writeTimeout = builder?.writeTimeout ?: client.writeTimeout
        }

        private fun checkRequest(request: Request) {
            if (request.scheme().isNullOrBlank() || request.host().isNullOrBlank()) {
                throw IllegalArgumentException("Request schema and host must not null.")
            }
        }

        class Builder {
            var connectTimeout: Int = -1
            var readTimeout: Int = -1
            var writeTimeout: Int = -1
        }
    }

    fun newCall(request: Request): Call {
        return Call(this, request, null)
    }

    fun newCall(request: Request, builder: Call.Builder): Call {
        return Call(this, request, builder)
    }

    /**
     * 这里有可能一个线程执行到同步代码块中，另一个在等待获取同步锁，因此需要在同步代码块中再次判断刷新状态，避免重复刷新。
     * 之所以不在方法上加同步锁，是因为这样每次都需要获取锁，通过先对状态的判断，可减少锁的使用。
     *
     * 这里需要先设置[refreshed]值为true再加载资源，因为类加载初始化阶段，会执行类构造器<clinit>() 方法，
     * 如果代码中会调用这个方法来加载类资源，且即将加载类的中也有静态属性或静态代码块会调用这个方法，那么[refreshed]值就会一直是false，
     * 进而会导致多次加载。
     */
    private fun refreshContext() {
        if (refreshed) {
            return
        }
        synchronized(Client::class) {
            if (refreshed) {
                return
            }
            refreshed = true
            annotatedResources = ContextScanner.scanAnnotatedResources(BASE_SCAN_PACKAGE)
            ContextScanner.processAnnotatedResources(annotatedResources)
        }
    }

    companion object {
        const val DEFAULT_TIMEOUT = 5000
        val BASE_SCAN_PACKAGE = this::class.packageName()
        var annotatedResources: List<AnnotatedKtResource> = listOf()
        @Volatile
        @JvmField
        var refreshed = false
    }
}