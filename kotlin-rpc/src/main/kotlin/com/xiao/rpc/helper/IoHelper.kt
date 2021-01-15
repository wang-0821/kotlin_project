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
    const val KILO = 1024
    const val BUFFER_SIZE = 8 * KILO
    const val CRLF = "\r\n"
    const val CARRIAGE_RETURN_BYTE = '\r'.toByte()
    const val LINE_FEED_BYTE = '\n'.toByte()
    private const val MAX_CACHE_SIZE = 32
    private val RPC_IO_BYTE_ARRAY = object : RpcContextKey<ByteArray> {}
    private val RPC_IO_CHAR_ARRAY = object : RpcContextKey<CharArray> {}

    @JvmStatic
    fun readPlainTextLine(
        inputStream: InputStream,
        charset: Charset = Charsets.UTF_8
    ): String {
        val byteArray = getByteArray()
        val charArray = getCharArray()
        val result = readLine(inputStream, byteArray, charArray, charset)
        cacheByteArray(byteArray)
        cacheCharArray(charArray)
        return result
    }

    @JvmStatic
    fun contentAsString(
        inputStream: InputStream,
        charset: Charset,
        length: Long = -1
    ): String {
        val byteArray = getByteArray()
        val charArray = getCharArray()
        val result = asString(inputStream, byteArray, charArray, charset, length) { input, bytes, offset, len ->
            input.read(bytes, offset, len)
        }
        cacheByteArray(byteArray)
        cacheCharArray(charArray)
        return result
    }

    private fun readLine(
        inputStream: InputStream,
        byteArray: ByteArray,
        charArray: CharArray,
        charset: Charset
    ): String {
        var buffer: PooledBuffer? = null
        val byteBuffer = ByteBuffer.wrap(byteArray)
        val charBuffer = CharBuffer.wrap(charArray)
        val charSetDecoder = charset.newDecoder()
        var isEnd = false
        while (true) {
            // fill byte array buffer
            val byteRemaining = byteBuffer.remaining()
            var alreadyRead = 0
            while (alreadyRead < byteRemaining - 2) {
                val nextByteCode = inputStream.read()
                // end of stream
                if (nextByteCode == -1) {
                    break
                }

                val nextByte = nextByteCode.toByte()
                // /r/n
                if (nextByte == CARRIAGE_RETURN_BYTE) {
                    val nextByte2 = inputStream.read().toByte()
                    if (nextByte2 == LINE_FEED_BYTE) {
                        isEnd = true
                        break
                    } else {
                        byteArray[alreadyRead++] = nextByte
                        byteArray[alreadyRead++] = nextByte2
                        if (alreadyRead >= byteArray.size) {
                            break
                        }
                    }
                } else {
                    // /n
                    if (nextByte == LINE_FEED_BYTE) {
                        isEnd = true
                        break
                    }
                    byteArray[alreadyRead++] = nextByte
                }
            }

            // already filled byte array buffer, decode byte array
            byteBuffer.position(byteBuffer.position() + alreadyRead)
            byteBuffer.flip()
            if (isEnd) {
                charSetDecoder.decode(byteBuffer, charBuffer, true)
                charBuffer.flip()
                // all chars are in single char array
                return if (buffer == null) {
                    String(charBuffer.array(), charBuffer.position(), charBuffer.remaining())
                } else {
                    buffer.appendCharBuffer(charBuffer)
                    charBuffer.clear()
                    byteBuffer.compact()
                    buffer.asString()
                }
            } else {
                charSetDecoder.decode(byteBuffer, charBuffer, false)
                charBuffer.flip()
                buffer = buffer ?: PooledBuffer()
                buffer.appendCharBuffer(charBuffer)
                charBuffer.clear()
                byteBuffer.compact()
            }
        }
    }

    private fun asString(
        inputStream: InputStream,
        byteArray: ByteArray,
        charArray: CharArray,
        charset: Charset,
        length: Long,
        readBlock: (InputStream, ByteArray, Int, Int) -> Int
    ): String {
        var buffer: PooledBuffer? = null
        val byteBuffer = ByteBuffer.wrap(byteArray)
        val charBuffer = CharBuffer.wrap(charArray)
        val charsetDecoder = charset.newDecoder()
        var total: Long = 0
        while (true) {
            val byteBufferRemaining = byteBuffer.remaining()
            val count = readBlock(inputStream, byteArray, byteBuffer.position(), byteBufferRemaining)
            if (count > 0) {
                total += count
                byteBuffer.position(byteBuffer.position() + count)
            }
            if (count > -1 && (length <= 0 || (length > 0 && total < length))) {
                byteBuffer.flip()
                charsetDecoder.decode(byteBuffer, charBuffer, false)
                charBuffer.flip()
                buffer = buffer ?: PooledBuffer()
                buffer.appendCharBuffer(charBuffer)
                charBuffer.clear()
                byteBuffer.compact()
            } else {
                check(length < 0 || total == length) {
                    "InputStream length $total is not equals with expected $length."
                }
                byteBuffer.flip()
                charsetDecoder.decode(byteBuffer, charBuffer, true)
                charBuffer.flip()
                return if (buffer == null) {
                    String(charBuffer.array(), charBuffer.position(), charBuffer.remaining())
                } else {
                    buffer.appendCharBuffer(charBuffer)
                    charBuffer.clear()
                    byteBuffer.compact()
                    buffer.asString()
                }
            }
        }
    }

    private fun getCharArray(): CharArray {
        val charArrayList = RpcHelper.fetch(RPC_IO_CHAR_ARRAY) {
            mutableListOf<CharArray>()
        }

        return if (charArrayList.isNotEmpty()) {
            charArrayList.removeAt(0)
        } else {
            CharArray(BUFFER_SIZE)
        }
    }

    private fun getByteArray(): ByteArray {
        val byteArrayList = RpcHelper.fetch(RPC_IO_BYTE_ARRAY) {
            mutableListOf<ByteArray>()
        }

        return if (byteArrayList.isNotEmpty()) {
            byteArrayList.removeAt(0)
        } else {
            ByteArray(BUFFER_SIZE)
        }
    }

    private fun cacheCharArray(charArray: CharArray): Boolean {
        val charArrayList = RpcHelper.fetch(RPC_IO_CHAR_ARRAY) {
            mutableListOf<CharArray>()
        }
        return if (charArrayList.size >= MAX_CACHE_SIZE) {
            false
        } else {
            charArrayList.add(charArray)
        }
    }

    private fun cacheByteArray(byteArray: ByteArray): Boolean {
        val byteArrayList = RpcHelper.fetch(RPC_IO_BYTE_ARRAY) {
            mutableListOf<ByteArray>()
        }
        return if (byteArrayList.size >= MAX_CACHE_SIZE) {
            return false
        } else {
            byteArrayList.add(byteArray)
        }
    }
}