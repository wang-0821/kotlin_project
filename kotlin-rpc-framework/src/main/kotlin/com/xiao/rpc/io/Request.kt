package com.xiao.rpc.io

import com.xiao.rpc.Protocol
import com.xiao.rpc.RequestMethod
import com.xiao.rpc.tool.UrlParser

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

    fun header(header: Header) {
        this.requestBuilder.headers.add(header)
    }

    fun  header(name: String): Header? {
        return requestBuilder.headers.firstOrNull { it.name == name }
    }

    fun headers(headers: List<Header>) {
        this.requestBuilder.headers.addAll(headers)
    }

    fun headers(): List<Header> {
        return this.requestBuilder.headers
    }

    fun param(name: String, value: String) {
        this.requestBuilder.requestParams[name] = value
    }

    fun params(params: Map<String, String>) {
        this.requestBuilder.requestParams.putAll(params)
    }

    private class RequestBuilder {
        lateinit var scheme: String
        lateinit var host: String
        var path: String? = null
        var port: Int = -1
        var method: RequestMethod = RequestMethod.GET
        var protocol: Protocol = Protocol.HTTP_1_1
        var headers: MutableList<Header> = mutableListOf()
        var requestParams: MutableMap<String, String> = mutableMapOf()
    }
}