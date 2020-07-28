package com.xiao.rpc

import com.xiao.base.context.ContextScanner
import com.xiao.rpc.handler.Chain
import com.xiao.rpc.handler.ReadTimeoutHandler
import com.xiao.rpc.handler.WriteTimeoutHandler
import com.xiao.rpc.tool.UrlParser

/**
 *
 * @author lix wang
 */
class Client {
    var connectTimeout = 5000
    private set
    var readTimeout = 5000
    private set
    var writeTimeout = 5000
    private set

    var readTimeoutHandler: ReadTimeoutHandler? = null
    private set
    var writeTimeoutHandler: WriteTimeoutHandler? = null
    private set

    init {
        refreshContext()
    }

    class Call(private val client: Client, private val request: Request) {
        fun execute(): Response {
             return Chain(client, request).execute()
        }
    }

    fun newCall(request: Request): Call {
        return Call(this, request)
    }

    fun connectTimeout(mills: Int): Client {
        this.connectTimeout = mills
        return this
    }

    fun readTimeout(mills: Int): Client {
        this.readTimeout = mills
        return this
    }

    fun writeTimeout(mills: Int): Client {
        this.writeTimeout = mills
        return this
    }

    fun readTimeoutHandler(handler: ReadTimeoutHandler): Client {
        this.readTimeoutHandler = handler
        return this
    }

    fun writeTimeoutHandler(handler: WriteTimeoutHandler): Client {
        this.writeTimeoutHandler = handler
        return this
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
    println(Thread.currentThread().contextClassLoader)
    Client().newCall(UrlParser.parseUrl("http://www.baidu.com")).execute()
}