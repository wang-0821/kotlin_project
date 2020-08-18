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
    private const val KILO = 1024

    /**
     * Default buffer size for small buffer stream, like http header.
     */
    const val BUFFER_SIZE = 4 * KILO
    const val CRLF = "\r\n"
    private const val CARRIAGE_RETURN_BYTE = '\r'.toByte()
    const val LINE_FEED_BYTE = '\n'.toByte()
    private val rpcIoByteArray = object : RpcContextKey<ByteArray> {}
    private val rpcIoCharArray = object : RpcContextKey<CharArray> {}

    fun readPlainTextLine(
        inputStream: InputStream,
        charset: Charset = Charsets.UTF_8
    ): String {
        val byteArray = getByteArray()
        val charArray = getCharArray()
        val result = asString(inputStream, byteArray, charArray, charset, -1)
        { input, bytes, offset, length ->
            readLineBuffer(input, bytes, offset, length)
        }
        cacheByteArray(byteArray)
        cacheCharArray(charArray)
        return result
    }

    @Throws(IllegalStateException::class)
    fun contentAsString(
        inputStream: InputStream,
        charset: Charset,
        length: Int = -1
    ): String {
        val byteArray = getByteArray()
        val charArray = getCharArray()
        val result = asString(inputStream, byteArray, charArray, charset, length)
        { input, bytes, offset, len ->
            input.read(bytes, offset, len)
        }
        cacheByteArray(byteArray)
        cacheCharArray(charArray)
        return result
    }

    private fun readLineBuffer(inputStream: InputStream, byteArray: ByteArray, offset: Int, length: Int): Int {
        var index = offset
        val endIndex = offset + length
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

    @Throws(IllegalStateException::class)
    private fun asString(
        inputStream: InputStream,
        byteArray: ByteArray,
        charArray: CharArray,
        charset: Charset,
        length: Int,
        readBlock: (InputStream, ByteArray, Int, Int) -> Int
    ): String {
        var buffer = PooledBuffer(byteArray.size)
        val byteBuffer = ByteBuffer.wrap(byteArray)
        val charBuffer = CharBuffer.wrap(charArray)
        val charsetDecoder = charset.newDecoder()
        var total = 0
        while (true) {
            val byteBufferRemaining = byteBuffer.remaining()
            val count = readBlock(inputStream, byteArray, byteBuffer.position(), byteBufferRemaining)
            if (count > -1) {
                byteBuffer.position(byteBuffer.position() + count)
                total += count
            }
            byteBuffer.flip()
            if (count >= byteBufferRemaining) {
                charsetDecoder.decode(byteBuffer, charBuffer, false)
                charBuffer.flip()
                buffer.appendCharBuffer(charBuffer)
                charBuffer.clear()
                byteBuffer.compact()
            } else {
                charsetDecoder.decode(byteBuffer, charBuffer, true)
                charBuffer.flip()
                buffer.let {
                    it.appendCharBuffer(charBuffer)
                    charBuffer.clear()
                    byteBuffer.compact()
                }
                if (length > 0 && total != length) {
                    throw IllegalStateException("InputStream length is not equals with expected.")
                }
                return buffer.asString()
            }
        }
    }

    private fun getCharArray(): CharArray {
        val charArrayList =  RpcHelper.fetch(rpcIoCharArray) {
            mutableListOf<CharArray>()
        }

        return if (charArrayList.isNotEmpty()) {
            charArrayList.removeAt(0)
        } else {
            CharArray(BUFFER_SIZE)
        }
    }

    private fun getByteArray(): ByteArray {
        val byteArrayList =  RpcHelper.fetch(rpcIoByteArray) {
            mutableListOf<ByteArray>()
        }

        return if (byteArrayList.isNotEmpty()) {
            byteArrayList.removeAt(0)
        } else {
            ByteArray(BUFFER_SIZE)
        }
    }

    private fun cacheCharArray(charArray: CharArray): Boolean {
        return RpcHelper.fetch(rpcIoCharArray) {
            mutableListOf<CharArray>()
        }.add(charArray)
    }

    private fun cacheByteArray(byteArray: ByteArray): Boolean {
        return RpcHelper.fetch(rpcIoByteArray) {
            mutableListOf<ByteArray>()
        }.add(byteArray)
    }
}