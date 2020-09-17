package com.xiao.rpc.helper

import com.xiao.rpc.ContentEncodingType
import com.xiao.rpc.ContentHeaders
import com.xiao.rpc.Protocol
import com.xiao.rpc.ResponseListener
import com.xiao.rpc.io.DefaultHttpResponseContent
import com.xiao.rpc.io.Header
import com.xiao.rpc.io.HttpResponseContent
import com.xiao.rpc.io.Response
import com.xiao.rpc.util.StreamUtils
import java.io.InputStream
import java.nio.charset.Charset

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

        // get content headers
        val contentEncoding = headers.lastOrNull {
            it.name.toUpperCase() == ContentHeaders.CONTENT_ENCODING.text.toUpperCase()
        }?.value?.let {
            ContentEncodingType.parse(it)
        } ?: ContentEncodingType.IDENTITY
        val transferEncoding = headers.lastOrNull {
            it.name.toUpperCase() == ContentHeaders.TRANSFER_ENCODING.text.toUpperCase()
        }?.value
        val contentLength = headers.lastOrNull {
            it.name.toUpperCase() == ContentHeaders.CONTENT_LENGTH.text.toUpperCase()
        }?.value?.toLong() ?: -1

        val contentType = headers.lastOrNull {
            it.name.toUpperCase() == ContentHeaders.CONTENT_TYPE.text.toUpperCase()
        }?.value

        return Response(
            protocol,
            status,
            headers,
            calculateResponseContent(inputStream, contentLength, contentType, contentEncoding, transferEncoding),
            responseListener
        )
    }

    private fun calculateResponseContent(
        inputStream: InputStream,
        contentLength: Long,
        contentType: String?,
        contentEncoding: ContentEncodingType,
        transferEncoding: String?
    ): HttpResponseContent {
        var charset: Charset? = null
        contentType?.let {
            val splits = it.split(";")
            for (split in splits) {
                if (split.trimStart().startsWith("charset")) {
                    charset = Charset.forName(split.split("=")[1].trimStart())
                }
            }
        }
        return DefaultHttpResponseContent(
            contentType,
            contentLength,
            charset,
            StreamUtils.calculateRealInputStream(
                inputStream, contentLength, contentEncoding, transferEncoding)
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