package com.xiao.rpc.io

import com.xiao.rpc.helper.IoHelper
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.zip.GZIPInputStream

/**
 *
 * @author lix wang
 */
class WrappedInputStream(
    private var inputStream: InputStream,
    private val contentEncoding: String? = null,
    private val transferEncoding: String? = null
) : InputStream() {
    private var chunkedLimit = -1
    private val chunkedByteArray = ByteArray(IoHelper.BUFFER_SIZE)
    private val chunkedBuffer = ByteBuffer.wrap(chunkedByteArray)
    private var realInputStream: InputStream? = null

    init {
        if (transferEncoding.isNullOrBlank() && "gzip" == contentEncoding) {
            inputStream = GZIPInputStream(inputStream)
        }
    }

    override fun read(): Int {
        return inputStream.read()
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return if ("chunked" == transferEncoding) {
            readChunked(b, off, len)
        } else {
            calculateSimpleInputStream().read(b, off, len)
        }
    }

    private fun readChunked(b: ByteArray, off: Int, len: Int): Int {
        val stream = calculateChunkedInputStream()
        val size = stream.read(b, off, len)
        if (size < len) {
            // reach chunk end
            realInputStream = null
        }
        return size
    }

    private fun calculateSimpleInputStream(): InputStream {
        if (realInputStream != null) {
            return realInputStream!!
        }
        realInputStream = if ("gzip" == contentEncoding) {
            GZIPInputStream(inputStream)
        } else {
            inputStream
        }
        return realInputStream as InputStream
    }

    private fun calculateChunkedInputStream(): InputStream {
        if (realInputStream != null) {
            return realInputStream!!
        }
        checkChunkLimit()
        return if ("gzip" == contentEncoding) {
            createChunkedGzipInputStream()
        } else {
            inputStream
        }
    }

    private fun createChunkedGzipInputStream(): InputStream {
        val remaining = chunkedBuffer.remaining()
        if (chunkedLimit <= remaining) {
            fillByteArray(chunkedLimit)
            while (inputStream.read().toByte() == IoHelper.LINE_FEED_BYTE) {
            }
        } else {
            fillByteArray(remaining)
        }
        val byteArrayInputStream = ByteArrayInputStream(
            chunkedBuffer.array(), chunkedBuffer.position(), chunkedBuffer.limit()
        )
        chunkedBuffer.clear()
        return GZIPInputStream(byteArrayInputStream)
    }

    private fun fillByteArray(len: Int) {
        var start = chunkedBuffer.position()
        for (i in 0..len) {
            chunkedByteArray[start++] = inputStream.read().toByte()
        }
        chunkedBuffer.position(start)
        chunkedBuffer.flip()
    }

    private fun checkChunkLimit() {
        if (chunkedLimit <= 0) {
            chunkedLimit = Integer.parseInt(IoHelper.readPlainTextLine(inputStream), 16)
        }
    }
}