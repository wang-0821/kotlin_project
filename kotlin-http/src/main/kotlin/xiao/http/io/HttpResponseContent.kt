package xiao.http.io

import java.io.Closeable
import java.io.InputStream

/**
 *
 * @author lix wang
 */
interface HttpResponseContent : Closeable {
    fun contentType(): String?

    fun contentLength(): Long

    fun content(): InputStream?

    fun asString(): String?
}