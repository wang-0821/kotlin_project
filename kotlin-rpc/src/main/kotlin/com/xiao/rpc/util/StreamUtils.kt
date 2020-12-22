package com.xiao.rpc.util

import com.xiao.rpc.Constants
import com.xiao.rpc.ContentEncodingType
import com.xiao.rpc.stream.ChunkedInputStream
import com.xiao.rpc.stream.ContentLengthInputStream
import com.xiao.rpc.stream.IdentityInputStream
import java.io.InputStream
import java.util.zip.GZIPInputStream

/**
 *
 * @author lix wang
 */
object StreamUtils {
    fun calculateRealInputStream(
        inputStream: InputStream,
        contentLength: Long,
        contentEncoding: ContentEncodingType,
        transferEncoding: String?
    ): InputStream {
        return when (contentEncoding) {
            ContentEncodingType.GZIP -> GZIPInputStream(
                calculateByTransfer(inputStream, contentLength, transferEncoding)
            )
            ContentEncodingType.IDENTITY -> IdentityInputStream(
                calculateByTransfer(inputStream, contentLength, transferEncoding)
            )
            else -> throw UnsupportedOperationException(
                "Unrecognized response streaming content with encoding $contentEncoding."
            )
        }
    }

    private fun calculateByTransfer(
        inputStream: InputStream,
        contentLength: Long,
        transferEncoding: String?
    ): InputStream {
        // not chunked data
        if (contentLength >= 0 && Constants.TRANSFER_ENCODING_CHUNKED != transferEncoding) {
            check(contentLength <= Int.MAX_VALUE) {
                "Streaming content length is oversize."
            }
            return ContentLengthInputStream(inputStream, contentLength.toInt())
        }

        // chunked data
        if (Constants.TRANSFER_ENCODING_CHUNKED == transferEncoding) {
            return ChunkedInputStream(inputStream)
        }

        // else
        throw UnsupportedOperationException(
            "Unrecognized streaming content with length: $contentLength, encoding: ${transferEncoding.orEmpty()}."
        )
    }
}