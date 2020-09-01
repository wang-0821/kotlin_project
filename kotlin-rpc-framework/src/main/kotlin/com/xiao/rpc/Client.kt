package com.xiao.rpc

import com.xiao.base.context.ContextScanner
import com.xiao.rpc.context.ClientContextPool
import com.xiao.rpc.context.DefaultClientContextPool
import com.xiao.rpc.handler.Chain
import com.xiao.rpc.io.Exchange
import com.xiao.rpc.io.Request
import com.xiao.rpc.io.Response
import com.xiao.rpc.tool.UrlParser
import com.xiao.rpc.tool.packageName

/**
 *
 * @author lix wang
 */
class Client {
    var connectTimeout = DEFAULT_TIMEOUT
    var readTimeout = DEFAULT_TIMEOUT
    var writeTimeout = DEFAULT_TIMEOUT
    var clientContextPool: ClientContextPool? = null
    private set

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

    fun clientContextPool(clientContextPool: ClientContextPool) {
        this.clientContextPool = clientContextPool
    }

    private fun refreshContext() {
        ContextScanner.scanAndExecute(BASE_SCAN_PACKAGE)
    }

    companion object  {
        const val DEFAULT_TIMEOUT = 5000
        val BASE_SCAN_PACKAGE = this::class.packageName()
    }
}

fun main() {
    val client = Client()
    val contextPool = DefaultClientContextPool()
    client.clientContextPool(contextPool)
    contextPool.start()

    val response = client.newCall(UrlParser.parseUrl("https://www.baidu.com")).execute()
    println("Response*********** ${response.contentAsString()} *******")
}