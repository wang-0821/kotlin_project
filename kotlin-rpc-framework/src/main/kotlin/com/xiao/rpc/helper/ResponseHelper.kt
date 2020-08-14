package com.xiao.rpc.helper

import com.xiao.rpc.ContentHeaders
import com.xiao.rpc.Protocol
import com.xiao.rpc.io.Header
import com.xiao.rpc.io.Response
import java.io.InputStream
import java.util.zip.GZIPInputStream

/**
 *
 * @author lix wang
 */
object ResponseHelper {
    fun parseResponse(inputStream: InputStream): Response {
        // parse startLine
        val startLine = IoHelper.readLine(inputStream)
        println("************ $startLine ************")
        val startLineSplits = startLine.split(" ")
        val protocol = Protocol.parseProtocol(startLineSplits[0])
        val status = startLineSplits[1].toInt()
        val headers = parseHeaders(inputStream)
        val contentEncoding = headers.firstOrNull {
            it.name.toUpperCase() == ContentHeaders.CONTENT_ENCODING.text.toUpperCase()
        }?.value
        val realInputStream = parseContent(contentEncoding, inputStream)
        return Response(protocol, status, headers, realInputStream)
    }

    private fun parseContent(contentEncoding: String?, inputStream: InputStream): InputStream {
        if ("gzip" == contentEncoding) {
            return GZIPInputStream(inputStream)
        }
        return inputStream
    }

    private fun parseHeaders(inputStream: InputStream): List<Header> {
        val headers = mutableListOf<Header>()
        while (true) {
            val line = IoHelper.readLine(inputStream)
            println("******** $line **********")
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