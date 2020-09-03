package com.xiao.rpc.helper

import com.xiao.rpc.ContentHeaders
import com.xiao.rpc.Protocol
import com.xiao.rpc.ResponseListener
import com.xiao.rpc.io.WrappedInputStream
import com.xiao.rpc.io.Header
import com.xiao.rpc.io.Response
import java.io.InputStream

/**
 *
 * @author lix wang
 */
object ResponseHelper {
    fun parseResponse(inputStream: InputStream, responseListener: ResponseListener): Response {
        val startLine = IoHelper.readPlainTextLine(inputStream)
        val startLineSplits = startLine.split(" ")
        val protocol = Protocol.parseProtocol(startLineSplits[0])
        val status = startLineSplits[1].toInt()
        val headers = parseHeaders(inputStream)
        val contentEncoding = headers.lastOrNull {
            it.name.toUpperCase() == ContentHeaders.CONTENT_ENCODING.text.toUpperCase()
        }?.value
        val transferEncoding = headers.lastOrNull {
            it.name.toUpperCase() == ContentHeaders.TRANSFER_ENCODING.text.toUpperCase()
        }?.value
        return Response(
            protocol,
            status,
            headers,
            WrappedInputStream(inputStream, contentEncoding, transferEncoding),
            responseListener
        )
    }

    private fun parseHeaders(inputStream: InputStream): List<Header> {
        val headers = mutableListOf<Header>()
        while (true) {
            val line = IoHelper.readPlainTextLine(inputStream)
            if (line.isNotBlank()) {
                parseHeader(line)?.let {
                    headers.add(it)
                }
            } else {
                break
            }
        }
        return headers
    }

    private fun parseHeader(headerLine: String): Header? {
        var keyIndex = -1
        for (index in headerLine.indices) {
            if (headerLine[index] == ':') {
                keyIndex = index
                break
            }
        }
        if (keyIndex > 0) {
            return Header(headerLine.substring(0, keyIndex), headerLine.substring(keyIndex + 1).trimStart())
        }
        return null
    }
}