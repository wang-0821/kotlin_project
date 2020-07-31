package com.xiao.rpc.io

import com.xiao.rpc.Server
import java.io.InputStream
import java.nio.charset.Charset

/**
 *
 * @author lix wang
 */
class BasicHttpEntity(private val inputStream: InputStream) : HttpEntity {
    override fun content(): InputStream {
        return inputStream
    }

    override fun contentAsString(): String {
        val readBytes = ByteArray(1024)
        var msgLen: Int
        val stringBuilder = StringBuilder()
        while (inputStream.read(readBytes).also { msgLen = it } != -1) {
            stringBuilder.append(String(readBytes, 0, msgLen, Charset.forName("UTF-8")))
        }
        println("get message from client: $stringBuilder")
        return stringBuilder.toString()
    }
}