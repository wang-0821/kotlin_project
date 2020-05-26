package com.xiao.rpc.protocol

import com.xiao.base.exception.KtException
import com.xiao.rpc.exception.UrlException

/**
 *
 * @author lix wang
 */
object UrlParser {
    @Throws(KtException::class)
    fun parseUrl(url: String): Request {
        try {
            var protocol: String? = null
            var host: String? = null
            var start = 0
            var index = 0
            var params: Map<String, String>? = null
            while (index < url.length) {
                if (url[index] == ':') {
                    if (index > 0 && index < url.length - 2 && url[index + 1] == url[index + 2]
                        && url[index + 1] == '/') {
                        protocol = url.substring(start until index)
                        start = index + 3
                        index = start
                        continue
                    }
                }

                if (host == null && url[index] == '/') {
                    host = url.substring(start until index)
                    start = index + 1
                }
                if (host == null && index == url.length - 1) {
                    host = url.substring(start..index)
                }
                if (url[index] == '?') {
                    if (host == null && start < index - 1) {
                        host = url.substring(start until index)
                        start = index + 1
                    }
                    params = parseParams(url.substring(start until url.length))
                    break
                }
                index++
            }

            val protocolType = ProtocolType.getType(protocol) ?: throw UrlException.noProtocol()
            if (host.isNullOrBlank()) {
                throw UrlException.unIdentifiedHost()
            }
            return Request(protocolType, host).also { request ->
                params?.let {
                    request.requestParams = it
                }
            }
        } catch (e: Exception) {
            throw UrlException.invalidFormat()
        }
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
                start = index + 1
            }
            if (paramString[index] == '&') {
                if (key.isNullOrBlank()) {
                    throw UrlException.invalidFormat()
                }
                result[key] = paramString.substring(start until index)
                start = index + 1
            }
            if (index == paramString.length - 1) {
                if (key.isNullOrBlank()) {
                    throw UrlException.invalidFormat()
                }
                result[key] = paramString.substring(start until paramString.length)
                break
            }
            index++
        }
        return result
    }
}
