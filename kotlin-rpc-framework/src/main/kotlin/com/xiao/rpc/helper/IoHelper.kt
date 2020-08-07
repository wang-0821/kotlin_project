package com.xiao.rpc.helper

import com.xiao.rpc.io.ByteArrayBuf
import java.io.InputStream

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
    const val DEFAULT_STREAM_BUFFER_SIZE = 8 * 1024

    const val CRLF = "\r\n"

    private const val CARRIAGE_RETURN_BYTE = '\r'.toByte()

    private const val LINE_FEED_BYTE = '\n'.toByte()

    fun readLine(inputStream: InputStream): String {
        val buffer = ByteArrayBuf()
        while (true) {
            val nextByteCode = inputStream.read()
            // end of stream
            if (nextByteCode == -1) {
                return buffer.toString()
            }

            val nextByte = nextByteCode.toByte()
            // /r/n
            if (nextByte == CARRIAGE_RETURN_BYTE) {
                val nextByte2 = inputStream.read().toByte()
                if (nextByte2 == LINE_FEED_BYTE) {
                    return buffer.toString()
                } else {
                    buffer.add(nextByte)
                    buffer.add(nextByte2)
                }
            }

            // /n
            if (nextByte == LINE_FEED_BYTE) {
                return buffer.toString()
            }
            buffer.add(nextByte)
        }
    }
}