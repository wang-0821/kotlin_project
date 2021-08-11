package xiao.base.util

import xiao.base.CommonConstants.BUFFER_SIZE
import xiao.base.io.CharBufferAdapter
import xiao.base.io.DefaultCharBufferAdapter
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset

/**
 *
 * @author lix wang
 */
object IoUtils {
    private const val CARRIAGE_RETURN_BYTE = '\r'.toByte()
    private const val LINE_FEED_BYTE = '\n'.toByte()
    const val CRLF = "\r\n"

    @JvmStatic
    fun readLine(
        inputStream: InputStream,
        charset: Charset = Charsets.UTF_8,
        byteArray: ByteArray? = null,
        charArray: CharArray? = null,
        charBufferAdapter: CharBufferAdapter? = null
    ): String? {
        val buffer = charBufferAdapter ?: DefaultCharBufferAdapter(BUFFER_SIZE)
        return buffer.use {
            readLine(
                inputStream,
                byteArray ?: ByteArray(BUFFER_SIZE),
                charArray ?: CharArray(BUFFER_SIZE),
                charset,
                buffer
            )
        }
    }

    @JvmStatic
    fun asString(
        inputStream: InputStream,
        charset: Charset = Charsets.UTF_8,
        length: Long = -1,
        byteArray: ByteArray? = null,
        charArray: CharArray? = null,
        charBufferAdapter: CharBufferAdapter? = null
    ): String? {
        val buffer = charBufferAdapter ?: DefaultCharBufferAdapter(BUFFER_SIZE)
        return buffer.use {
            asString(
                inputStream,
                byteArray ?: ByteArray(BUFFER_SIZE),
                charArray ?: CharArray(BUFFER_SIZE),
                charset,
                length,
                buffer
            ) { input, bytes, offset, len ->
                input.read(bytes, offset, len)
            }
        }
    }

    private fun readLine(
        inputStream: InputStream,
        byteArray: ByteArray,
        charArray: CharArray,
        charset: Charset,
        charBufferAdapter: CharBufferAdapter
    ): String? {
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
                    // no content read
                    if (alreadyRead <= 0) {
                        null
                    } else {
                        String(charBuffer.array(), charBuffer.position(), charBuffer.remaining())
                    }
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
    ): String? {
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
                // all chars read in single read block.
                return if (charBufferAdapter.size() <= 0) {
                    // no content read
                    if (total <= 0) {
                        null
                    } else {
                        String(charBuffer.array(), charBuffer.position(), charBuffer.remaining())
                    }
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