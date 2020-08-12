package com.xiao.rpc.helper

import com.xiao.rpc.io.PooledBuffer
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset

/**
 *
 * @author lix wang
 */
object IoHelper {
    /**
     * Default buffer size for small buffer stream, like http header.
     */
    const val BUFFER_SIZE = 4 * 1024
    /**
     * Default buffer size for buffered streams, like http entity.
     */
    const val STREAM_BUFFER_SIZE = 8 * 1024

    const val CRLF = "\r\n"

    private const val CARRIAGE_RETURN_BYTE = '\r'.toByte()

    private const val LINE_FEED_BYTE = '\n'.toByte()

    fun readLine(inputStream: InputStream, charset: Charset = Charsets.UTF_8): String {
        return asString(inputStream, BUFFER_SIZE, charset, -1) { inputStream, bytes, offset, limit ->
            readLineBuffer(inputStream, bytes, offset, limit)
        }
    }

    @Throws(IllegalStateException::class)
    fun inputStreamToString(inputStream: InputStream, charset: Charset, length: Int = -1): String {
        return asString(inputStream, STREAM_BUFFER_SIZE, charset, length) { inputStream, bytes, offset, limit ->
            read(inputStream, bytes, offset, limit)
        }
    }

    @Throws(IllegalStateException::class)
    private fun asString(
        inputStream: InputStream,
        bufferSize: Int,
        charset: Charset,
        length: Int,
        readBlock: (InputStream, ByteArray, Int, Int) -> Int
    ): String {
        val buffer = PooledBuffer(bufferSize)
        val byteArray = ByteArray(bufferSize)
        val charArray = CharArray(bufferSize)
        val charBuffer = CharBuffer.wrap(charArray)
        val byteBuffer = ByteBuffer.wrap(byteArray)
        val charsetDecoder = charset.newDecoder()
        var total = 0
        while (true) {
            val count = readBlock(inputStream, byteArray, byteBuffer.position(), byteBuffer.remaining())
            total += count
            if (count > byteBuffer.remaining()) {
                byteBuffer.position(byteBuffer.position() + count)
                byteBuffer.flip()
                charsetDecoder.decode(byteBuffer, charBuffer, false)
                charBuffer.flip()
                buffer.appendCharBuffer(charBuffer)
                charBuffer.clear()
                byteBuffer.compact()
            } else {
                byteBuffer.flip()
                charsetDecoder.decode(byteBuffer, charBuffer, true)
                buffer.appendCharBuffer(charBuffer)
                charBuffer.clear()
                byteBuffer.compact()
                if (length > 0 && total != length) {
                    throw IllegalStateException("InputStream length is not equals with expected.")
                }
                return  buffer.asString()
            }
        }
    }

    private fun readLineBuffer(inputStream: InputStream, byteArray: ByteArray, offset: Int, limit: Int): Int {
        var index = offset
        val endIndex = offset + limit
        while (index <= endIndex - 2) {
            val nextByteCode = inputStream.read()
            // end of stream
            if (nextByteCode == -1) {
                return index - offset
            }

            val nextByte = nextByteCode.toByte()
            // /r/n
            if (nextByte == CARRIAGE_RETURN_BYTE) {
                val nextByte2 = inputStream.read().toByte()
                if (nextByte2 == LINE_FEED_BYTE) {
                    return index - offset
                } else {
                    byteArray[index++] = nextByte
                    byteArray[index++] = nextByte2
                    if (index >= byteArray.size) {
                        return index - offset
                    }
                }
            } else {
                // /n
                if (nextByte == LINE_FEED_BYTE) {
                    return index - offset
                }
                byteArray[index++] = nextByte
            }
        }

        // get last byte
        val nextByteCode = inputStream.read()
        if (nextByteCode == -1) {
            return index - offset
        }
        val nextByte = nextByteCode.toByte()
        if (nextByte != LINE_FEED_BYTE) {
            byteArray[index++] = nextByte
        }
        return index - offset
    }

    private fun read(
        inputStream: InputStream,
        byteArray: ByteArray,
        offset: Int,
        limit: Int
    ): Int {
        var index = offset
        val endIndex = offset + limit
        while (true) {
            val nextByteCode = inputStream.read()
            if (nextByteCode == -1) {
                return index - offset
            } else {
                if (index < endIndex) {
                    byteArray[index++] = nextByteCode.toByte()
                } else {
                    return index - offset
                }
            }
        }
    }
}