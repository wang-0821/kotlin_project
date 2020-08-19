package com.xiao.rpc.io

import com.xiao.rpc.helper.IoHelper
import java.io.InputStream
import java.nio.ByteBuffer

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
            inputStream = WrappedGZIPInputStream(inputStream)
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
        chunkLimitCheck()
        val inputStream = calculateChunkedInputStream()
        return if ("gzip" == contentEncoding) {
            // check chunked gzip inputStream
            checkGzipInputStream(inputStream)
            inputStream.read(b, off, len)
        } else {
            inputStream.read(b, off, len.coerceAtMost(chunkedLimit))
        }
    }

    private fun checkGzipInputStream(inputStream: InputStream) {
        val gzipInputStream = inputStream as? WrappedGZIPInputStream
        val byteArrayInputStream = gzipInputStream?.inputStream() as? WrappedByteArrayInputStream
        byteArrayInputStream?.let {
            if (it.available() <= 0) {
                fillChunkedByteBuffer()
                it.replace(chunkedBuffer.array(), chunkedBuffer.position(), chunkedBuffer.remaining())
                chunkedBuffer.clear()
            }
        }
    }

   private fun calculateChunkedInputStream(): InputStream {
       if (realInputStream != null) {
           return realInputStream as InputStream
       }
       realInputStream = if ("gzip" == contentEncoding) {
           createChunkedGzipInputStream()
       } else {
           inputStream
       }
       return realInputStream as InputStream
   }

    private fun calculateSimpleInputStream(): InputStream {
        if (realInputStream != null) {
            return realInputStream!!
        }
        realInputStream = if ("gzip" == contentEncoding) {
            WrappedGZIPInputStream(inputStream)
        } else {
            inputStream
        }
        return realInputStream as InputStream
    }

    private fun createChunkedGzipInputStream(): InputStream {
        fillChunkedByteBuffer()
        val byteArrayInputStream = WrappedByteArrayInputStream(
            chunkedBuffer.array(), chunkedBuffer.position(), chunkedBuffer.remaining()
        )
        chunkedBuffer.clear()
        return WrappedGZIPInputStream(byteArrayInputStream)
    }

    private fun fillChunkedByteBuffer() {
        val remaining = chunkedBuffer.remaining()
        if (chunkedLimit <= remaining) {
            fillByteArray(chunkedLimit)
            while (inputStream.read().toByte() == IoHelper.LINE_FEED_BYTE) {
            }
        } else {
            fillByteArray(remaining)
        }
    }

    private fun fillByteArray(len: Int) {
        var start = chunkedBuffer.position()
        for (i in 0 until len) {
            chunkedByteArray[start++] = inputStream.read().toByte()
        }
        chunkedBuffer.position(start)
        chunkedBuffer.flip()
    }

    private fun chunkLimitCheck(){
        if (chunkedLimit <= 0) {
            chunkedLimit = Integer.parseInt(IoHelper.readPlainTextLine(inputStream), 16)
        }
    }
}