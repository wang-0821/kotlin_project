package com.xiao.rpc

import com.xiao.base.context.ContextScanner
import com.xiao.rpc.context.ClientContextPool
import com.xiao.rpc.handler.Chain
import com.xiao.rpc.io.Request
import com.xiao.rpc.io.Response
import com.xiao.rpc.tool.UrlParser

/**
 *
 * @author lix wang
 */
class Client {
    var connectTimeout = 5000
    var readTimeout = 5000
    var writeTimeout = 5000
    var clientContextPool: ClientContextPool? = null
    private set

    init {
        refreshContext()
    }

    class Call(private val client: Client, private val request: Request) {
        fun execute(): Response {
            checkRequest(request)
             return Chain(client, request).execute()
        }

        private fun checkRequest(request: Request) {
            if (request.scheme().isNullOrBlank() || request.host().isNullOrBlank()) {
                throw IllegalArgumentException("Request schema and host must not null.")
            }
        }
    }

    fun newCall(request: Request): Call {
        return Call(this, request)
    }

    fun clientContext(clientContextPool: ClientContextPool) {
        this.clientContextPool = clientContextPool
    }

    private fun refreshContext() {
        ContextScanner.scanAndExecute(getPackageName(this::class.java.name))
    }

    companion object  {
        private fun getPackageName(fqClassName: String): String {
            val lastDotIndex: Int = fqClassName.lastIndexOf(".")
            return if (lastDotIndex != -1) fqClassName.substring(0, lastDotIndex) else ""
        }
    }
}

fun main() {
    val response = Client().newCall(UrlParser.parseUrl("https://www.baidu.com")).execute()
    println("Response*********** ${response.contentAsString()} *******")
}