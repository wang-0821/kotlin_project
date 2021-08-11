package xiao.http.helper

import xiao.base.util.IoUtils
import xiao.http.ContentEncodingType
import xiao.http.ContentHeaders
import xiao.http.Protocol
import xiao.http.ResponseListener
import xiao.http.io.DefaultHttpResponseContent
import xiao.http.io.Header
import xiao.http.io.HttpResponseContent
import xiao.http.io.Response
import xiao.http.util.StreamUtils
import java.io.InputStream
import java.net.Socket
import java.nio.charset.Charset

/**
 *
 * @author lix wang
 */
object ResponseHelper {
    fun parseResponse(inputStream: InputStream, responseListener: ResponseListener, socket: Socket): Response {
        val startLine = IoUtils.readLine(inputStream)
        val startLineSplits = startLine!!.split(" ")
        val protocol = Protocol.parseProtocol(startLineSplits[0])
        val status = startLineSplits[1].toInt()
        val headers = parseHeaders(inputStream)

        // get content headers
        val contentEncoding = headers.lastOrNull {
            it.name.equals(ContentHeaders.CONTENT_ENCODING.text, ignoreCase = true)
        }?.value?.let {
            ContentEncodingType.parse(it)
        } ?: ContentEncodingType.IDENTITY
        val transferEncoding = headers.lastOrNull {
            it.name.equals(ContentHeaders.TRANSFER_ENCODING.text, ignoreCase = true)
        }?.value
        val contentLength = headers.lastOrNull {
            it.name.equals(ContentHeaders.CONTENT_LENGTH.text, ignoreCase = true)
        }?.value?.toLong() ?: -1

        val contentType = headers.lastOrNull {
            it.name.equals(ContentHeaders.CONTENT_TYPE.text, ignoreCase = true)
        }?.value

        return Response(
            protocol,
            status,
            headers,
            calculateResponseContent(inputStream, contentLength, contentType, contentEncoding, transferEncoding),
            responseListener,
            socket
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
                inputStream, contentLength, contentEncoding, transferEncoding
            )
        )
    }

    private fun parseHeaders(inputStream: InputStream): List<Header> {
        val headers = mutableListOf<Header>()
        while (true) {
            val line = IoUtils.readLine(inputStream)
            if (line.isNullOrBlank()) {
                break
            } else {
                parseHeader(line)?.let {
                    headers.add(it)
                }
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