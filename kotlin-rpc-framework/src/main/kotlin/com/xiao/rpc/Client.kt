package com.xiao.rpc

import com.xiao.rpc.exception.ConnectionException
import com.xiao.rpc.handler.Chain

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
    var socketFactory: SocketFactory = DefaultSocketFactory
    private set

    var connectTimeoutHandler: ((ConnectionException.ConnectTimeoutExceptionConnection) -> Unit)? = null
    private set
    var readTimeoutHandler: ((ConnectionException.ReadTimeoutExceptionConnection) -> Unit)? = null
    private set
    var writeTimeoutHandler: ((ConnectionException.WriteTimeoutExceptionConnection) -> Unit)? = null
    private set

    init {
        refreshContext()
    }

    class Call(private val client: Client, private val request: Request) {
        fun execute(): Response {
            val chain = Chain(client, request)
            chain.initHandlers()
            chain.initContext()
            return chain.execute()
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

    fun connectTimeoutHandler(handler: (ConnectionException.ConnectTimeoutExceptionConnection) -> Unit): Client {
        this.connectTimeoutHandler = handler
        return this
    }

    fun readTimeoutHandler(handler: (ConnectionException.ReadTimeoutExceptionConnection) -> Unit): Client {
        this.readTimeoutHandler = handler
        return this
    }

    fun writeTimeoutHandler(handler: (ConnectionException.WriteTimeoutExceptionConnection) -> Unit): Client {
        this.writeTimeoutHandler = handler
        return this
    }

    fun socketFactory(socketFactory: SocketFactory): Client {
        this.socketFactory = socketFactory
        return this
    }

    private fun refreshContext() {
        ContextScanner.doScan()
    }
}

fun main() {
    println(Thread.currentThread().contextClassLoader)
    Client().newCall(UrlParser.parseUrl("http://www.baidu.com")).execute()
}