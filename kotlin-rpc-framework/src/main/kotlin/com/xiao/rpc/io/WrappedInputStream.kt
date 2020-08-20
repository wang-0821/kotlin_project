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
    private val chunkedByteArray = ByteArray(IoHelper.BUFFER_SIZE)
    private val chunkedBuffer = ByteBuffer.wrap(chunkedByteArray)
    private var realInputStream: InputStream? = null
    private var chunkedLimit: Int = -1
    private var endInputStream = false

    override fun read(): Int {
        return inputStream.read()
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return if ("chunked" == transferEncoding) {
            calculateChunkedInputStream().read(b, off, len)
        } else {
            calculateSimpleInputStream().read(b, off, len)
        }
    }

   private fun calculateChunkedInputStream(): InputStream {
       if (realInputStream != null) {
           checkChunkedInputStreamState(realInputStream!!)
           return realInputStream as InputStream
       }
       realInputStream = if ("gzip" == contentEncoding) {
           createChunkedGzipInputStream()
       } else {
           createChunkedSimpleInputStream()
       }
       return realInputStream as InputStream
   }

    private fun calculateSimpleInputStream(): InputStream {
        if (realInputStream != null) {
            return realInputStream!!
        }
        realInputStream = if ("gzip" == contentEncoding) {
            WrappedGZIPInputStream(inputStream, IoHelper.BUFFER_SIZE)
        } else {
            inputStream
        }
        return realInputStream as InputStream
    }

    private fun checkChunkedInputStreamState(inputStream: InputStream) {
        when (inputStream) {
            is WrappedGZIPInputStream -> checkChunkedGzipInputStream(inputStream)
            is WrappedByteArrayInputStream -> checkChunkedByteArrayInputStream(inputStream)
        }
    }

    private fun checkChunkedGzipInputStream(inputStream: WrappedGZIPInputStream) {
        val byteArrayInputStream = inputStream.inputStream() as? WrappedByteArrayInputStream
        byteArrayInputStream?.let {
            checkChunkedByteArrayInputStream(it)
        }
    }

    private fun checkChunkedByteArrayInputStream(inputStream: WrappedByteArrayInputStream) {
        if (inputStream.available() <= 0) {
            fillChunkedByteBuffer()
            inputStream.replace(chunkedBuffer.array(), chunkedBuffer.position(), chunkedBuffer.remaining())
            chunkedBuffer.clear()
        }
    }

    private fun createChunkedSimpleInputStream(): InputStream {
        fillChunkedByteBuffer()
        val input = WrappedByteArrayInputStream(
            chunkedBuffer.array(), chunkedBuffer.position(), chunkedBuffer.remaining()
        )
        chunkedBuffer.clear()
        return input
    }

    private fun createChunkedGzipInputStream(): InputStream {
        return WrappedGZIPInputStream(createChunkedSimpleInputStream(), IoHelper.BUFFER_SIZE)
    }

    @Throws(IllegalStateException::class)
    private fun fillChunkedByteBuffer() {
        calculateChunkLimit()
        if (chunkedLimit <= 0) {
            chunkedBuffer.position(0)
            chunkedBuffer.flip()
            return
        }
        val capacity = chunkedBuffer.capacity()
        if (chunkedLimit <= capacity) {
            fillByteArray(chunkedLimit)
            chunkedLimit = 0

            // make sure inputStream reached chunked end
            val byte1 = inputStream.read().toByte()
            if (byte1 == IoHelper.CARRIAGE_RETURN_BYTE) {
                if (inputStream.read().toByte() == IoHelper.LINE_FEED_BYTE) {
                    return
                }
            }
            if (byte1 == IoHelper.LINE_FEED_BYTE) {
                return
            }
            throw IllegalStateException("Chunked length is incorrect.")
        } else {
            fillByteArray(capacity)
            chunkedLimit -= capacity
        }
    }

    private fun fillByteArray(len: Int) {
        var start = 0
        for (i in 0 until len) {
            chunkedByteArray[start++] = inputStream.read().toByte()
        }
        chunkedBuffer.position(start)
        chunkedBuffer.flip()
    }

    private fun calculateChunkLimit() {
        if (chunkedLimit <= 0 && !endInputStream) {
            chunkedLimit = Integer.parseInt(IoHelper.readPlainTextLine(inputStream), 16)
            println("*********** chunkedLimit: $chunkedLimit ************")
            if (chunkedLimit <= 0) {
                endInputStream = true
            }
        }
    }
}