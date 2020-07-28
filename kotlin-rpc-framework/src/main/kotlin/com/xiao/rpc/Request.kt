package com.xiao.rpc

import com.xiao.rpc.tool.UrlParser
import kotlin.properties.Delegates

/**
 *
 * @author lix wang
 */
@Suppress("UNCHECKED_CAST")
class Request {
    private var requestBuilder: RequestBuilder

    constructor() {
        this.requestBuilder = RequestBuilder()
    }

    constructor(url: String) : this() {
        UrlParser.parseUrl(url, this)
    }

    fun method(method: RequestMethod) {
        this.requestBuilder.method = method
    }

    fun method(): RequestMethod {
        return this.requestBuilder.method
    }

    fun scheme(scheme: String) {
        this.requestBuilder.scheme = scheme
    }

    fun scheme(): String {
        return this.requestBuilder.scheme
    }

    fun host(host: String) {
        this.requestBuilder.host = host
    }

    fun host(): String {
        return this.requestBuilder.host
    }

    fun path(path: String) {
        this.requestBuilder.path = path
    }
    fun path(): String? {
        return this.requestBuilder.path
    }

    fun port(port: Int) {
        this.requestBuilder.port = port
    }

    fun port(): Int {
        return this.requestBuilder.port
    }
    fun protocol(protocol: Protocol) {
        this.requestBuilder.protocol = protocol
    }

    fun protocol(): Protocol {
        return this.requestBuilder.protocol
    }

    fun header(name: String, value: Any) {
        this.requestBuilder.headers[name] = value
    }

    fun <T> header(name: String): T? {
        return requestBuilder.headers[name] as? T
    }

    fun headers(headers: Map<String, Any>) {
        this.requestBuilder.headers.putAll(headers)
    }

    fun param(name: String, value: Any) {
        initParams()
        this.requestBuilder.requestParams!![name] = value
    }

    fun params(params: Map<String, Any>) {
        initParams()
        this.requestBuilder.requestParams!!.putAll(params)
    }

    private fun initParams() {
        if (this.requestBuilder.requestParams == null) {
            this.requestBuilder.requestParams = mutableMapOf()
        }
    }

    private class RequestBuilder {
        lateinit var scheme: String
        lateinit var host: String
        var path: String? = null
        var port by Delegates.notNull<Int>()
        var method: RequestMethod = RequestMethod.GET
        var protocol: Protocol = Protocol.HTTP_1_1
        var headers: MutableMap<String, Any> = mutableMapOf()
        var requestParams: MutableMap<String, Any>? = null
    }
}