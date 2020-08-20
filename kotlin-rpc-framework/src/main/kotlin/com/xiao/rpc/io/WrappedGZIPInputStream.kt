package com.xiao.rpc.io

import java.io.InputStream
import java.util.zip.GZIPInputStream

/**
 *
 * @author lix wang
 */
internal class WrappedGZIPInputStream : GZIPInputStream {
    constructor(inputStream: InputStream, size: Int): super(inputStream, size)

    internal fun inputStream(): InputStream {
        return this.`in`
    }
}