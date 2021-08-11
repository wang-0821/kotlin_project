package xiao.http.io

import xiao.http.Protocol
import xiao.http.RequestMethod
import xiao.http.util.UrlParser

/**
 *
 * @author lix wang
 */
@Suppress("UNCHECKED_CAST")
class Request {
    private var requestBuilder: RequestBuilder

    constructor() {
        requestBuilder = RequestBuilder()
    }

    constructor(url: String) : this() {
        UrlParser.parseUrl(url, this)
    }

    fun method(method: RequestMethod) {
        requestBuilder.method = method
    }

    fun method(): RequestMethod {
        return requestBuilder.method
    }

    fun scheme(scheme: String) {
        requestBuilder.scheme = scheme
    }

    fun scheme(): String? {
        return requestBuilder.scheme
    }

    fun host(host: String) {
        requestBuilder.host = host
    }

    fun host(): String? {
        return requestBuilder.host
    }

    fun path(path: String) {
        requestBuilder.path = path
    }
    fun path(): String? {
        return requestBuilder.path
    }

    fun port(port: Int) {
        requestBuilder.port = port
    }

    fun port(): Int {
        return requestBuilder.port
    }
    fun protocol(protocol: Protocol) {
        requestBuilder.protocol = protocol
    }

    fun protocol(): Protocol {
        return requestBuilder.protocol
    }

    fun header(header: Header) {
        requestBuilder.headers.add(header)
    }

    fun header(name: String): Header? {
        return requestBuilder.headers.lastOrNull { it.name == name }
    }

    fun headers(headers: List<Header>) {
        requestBuilder.headers.addAll(headers)
    }

    fun headers(): List<Header> {
        return requestBuilder.headers
    }

    fun param(name: String, value: String) {
        requestBuilder.requestParams[name] = value
    }

    fun params(params: Map<String, String>) {
        requestBuilder.requestParams.putAll(params)
    }

    fun params(): Map<String, String> {
        return requestBuilder.requestParams
    }

    private class RequestBuilder {
        var scheme: String? = null
        var host: String? = null
        var path: String? = null
        var port: Int = -1
        var method: RequestMethod = RequestMethod.GET
        var protocol: Protocol = Protocol.HTTP_1_1
        var headers: MutableList<Header> = mutableListOf()
        var requestParams: MutableMap<String, String> = mutableMapOf()
    }
}