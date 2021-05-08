package com.xiao.base.util

import com.xiao.base.io.CharBufferAdapter
import com.xiao.base.io.DefaultCharBufferAdapter
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset

/**
 *
 * @author lix wang
 */
object IoUtils {
    private const val KILO = 1024
    private const val CARRIAGE_RETURN_BYTE = '\r'.toByte()
    private const val LINE_FEED_BYTE = '\n'.toByte()
    const val BUFFER_SIZE = 8 * KILO
    const val CRLF = "\r\n"

    @JvmStatic
    fun readLine(
        inputStream: InputStream,
        charset: Charset = Charsets.UTF_8,
        byteArray: ByteArray? = null,
        charArray: CharArray? = null,
        charBufferAdapter: CharBufferAdapter? = null
    ): String {
        val buffer = charBufferAdapter ?: DefaultCharBufferAdapter(BUFFER_SIZE)
        try {
            return readLine(
                inputStream,
                byteArray ?: ByteArray(BUFFER_SIZE),
                charArray ?: CharArray(BUFFER_SIZE),
                charset,
                buffer
            )
        } finally {
            buffer.close()
        }
    }

    @JvmStatic
    fun asString(
        inputStream: InputStream,
        charset: Charset,
        length: Long = -1,
        byteArray: ByteArray? = null,
        charArray: CharArray? = null,
        charBufferAdapter: CharBufferAdapter? = null
    ): String {
        val buffer = charBufferAdapter ?: DefaultCharBufferAdapter(BUFFER_SIZE)
        try {
            return asString(
                inputStream,
                byteArray ?: ByteArray(BUFFER_SIZE),
                charArray ?: CharArray(BUFFER_SIZE),
                charset,
                length,
                buffer
            ) { input, bytes, offset, len ->
                input.read(bytes, offset, len)
            }
        } finally {
            buffer.close()
        }
    }

    private fun readLine(
        inputStream: InputStream,
        byteArray: ByteArray,
        charArray: CharArray,
        charset: Charset,
        charBufferAdapter: CharBufferAdapter
    ): String {
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
                return if (charBufferAdapter.size() <= 0) {
                    String(charBuffer.array(), charBuffer.position(), charBuffer.remaining())
                } else {
                    charBufferAdapter.appendCharBuffer(charBuffer)
                    charBuffer.clear()
                    byteBuffer.compact()
                    charBufferAdapter.asString()
                }
            } else {
                charSetDecoder.decode(byteBuffer, charBuffer, false)
                charBuffer.flip()
                charBufferAdapter.appendCharBuffer(charBuffer)
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
        charBufferAdapter: CharBufferAdapter,
        readBlock: (InputStream, ByteArray, Int, Int) -> Int
    ): String {
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
                charBufferAdapter.appendCharBuffer(charBuffer)
                charBuffer.clear()
                byteBuffer.compact()
            } else {
                check(length < 0 || total == length) {
                    "InputStream length $total is not equals with expected $length."
                }
                byteBuffer.flip()
                charsetDecoder.decode(byteBuffer, charBuffer, true)
                charsetDecoder.flush(charBuffer)
                charBuffer.flip()
                return if (charBufferAdapter.size() <= 0) {
                    String(charBuffer.array(), charBuffer.position(), charBuffer.remaining())
                } else {
                    charBufferAdapter.appendCharBuffer(charBuffer)
                    charBuffer.clear()
                    byteBuffer.compact()
                    charBufferAdapter.asString()
                }
            }
        }
    }
}