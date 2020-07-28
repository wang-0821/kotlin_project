package com.xiao.rpc.tool

import com.xiao.base.exception.KtException
import com.xiao.rpc.Request
import com.xiao.rpc.exception.UrlException

/**
 *
 * @author lix wang
 */
object UrlParser {
    @Throws(KtException::class)
    fun parseUrl(url: String): Request {
        return parseUrl(url, null)
    }

    @Throws(KtException::class)
    fun parseUrl(url: String, request: Request?): Request {
        var scheme: String? = null
        var host: String? = null
        var path: String? = null
        var params: Map<String, String>? = null

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
            if (host == null && url[index] == '/') {
                host = url.substring(start until index)
                start = index
            }
            if (host == null && index == url.length - 1) {
                host = url.substring(start..index)
            }

            // get path and params
            if (url[index] == '?') {
                if (host == null) {
                    host = url.substring(start until index)
                } else {
                    path = url.substring(start until index)
                }
                params = parseParams(url.substring(start until url.length))
                break
            }
            index++
        }

        if (scheme.isNullOrBlank()) {
            throw UrlException.noScheme()
        }
        if (host.isNullOrBlank()) {
            throw UrlException.noHost()
        }

        val realRequest = request ?: Request()
        realRequest.scheme(scheme)
        realRequest.host(host)
        path?.let {
            realRequest.path(path)
        }
        params?.let {
            realRequest.params(params)
        }
        return realRequest
    }

    @Throws(KtException::class)
    private fun parseParams(paramString: String): Map<String, String>? {
        if (paramString.isNullOrBlank()) {
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
                    UrlException.invalidParamFormat()
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
