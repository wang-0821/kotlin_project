package com.xiao.rpc.tool

import com.xiao.rpc.io.Request

/**
 *
 * @author lix wang
 */
object UrlParser {
    fun parseUrl(url: String): Request {
        return parseUrl(url, null)
    }

    fun parseUrl(url: String, request: Request?): Request {
        var scheme: String? = null
        var hostAndPort: String? = null
        var path: String? = null
        var params: Map<String, String>? = null
        var host: String?
        var port: Int? = null

        var start = 0
        var index = 0
        while (index < url.length) {
            // get scheme
            if (url[index] == ':') {
                if (index > 0 && index < url.length - 2 && url[index + 1] == url[index + 2]
                    && url[index + 1] == '/') {
                    scheme = url.substring(start until index)
                    start = index + 3
                    index = start
                    continue
                }
            }

            // get host
            if (hostAndPort == null && url[index] == '/') {
                hostAndPort = url.substring(start until index)
                start = index
            }
            if (hostAndPort == null && index == url.length - 1) {
                hostAndPort = url.substring(start..index)
            }

            // get path and params
            if (url[index] == '?') {
                if (hostAndPort == null) {
                    hostAndPort = url.substring(start until index)
                } else {
                    path = url.substring(start until index)
                }
                params = parseParams(url.substring(start until url.length))
                break
            }
            index++
        }

        val realRequest = request ?: Request()
        host = hostAndPort
        if (!hostAndPort.isNullOrBlank()) {
            for (i in hostAndPort.indices) {
                if (hostAndPort[i] == ':') {
                    host = hostAndPort.substring(0 until i)
                    port = hostAndPort.substring(i + 1 until hostAndPort.length).toInt()
                    break
                }
            }
        }


        scheme?.let {
            realRequest.scheme(it)
        }
        host?.let {
            realRequest.host(it)
        }
        port?.let {
            realRequest.port(it)
        }
        path?.let {
            realRequest.path(it)
        }
        params?.let {
            realRequest.params(it)
        }
        return realRequest
    }

    private fun parseParams(paramString: String): Map<String, String>? {
        if (paramString.isBlank()) {
            return null
        }

        val result = mutableMapOf<String, String>()
        var start = 0
        var index = 0
        var key = ""
        while (index < paramString.length) {
            if (paramString[index] == '=') {
                key = paramString.substring(start until index)
                if (key.isBlank()) {
                    throw IllegalArgumentException("Http request params format is illegal.")
                }
                start = index + 1
            }
            if (paramString[index] == '&') {
                result[key] = paramString.substring(start until index)
                start = index + 1
            }
            if (index == paramString.length - 1) {
                result[key] = paramString.substring(start until paramString.length)
                break
            }
            index++
        }
        return result
    }
}
