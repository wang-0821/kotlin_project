package com.xiao.rpc.helper

import com.xiao.base.exception.HttpStatus
import com.xiao.rpc.Protocol
import com.xiao.rpc.io.Response
import okhttp3.internal.closeQuietly
import java.io.InputStream

/**
 *
 * @author lix wang
 */
object ResponseHelper {
    fun parseResponse(inputStream: InputStream): Response {
        val startLine = IoHelper.readLine(inputStream)
        val headerLines = mutableListOf<String>()
        while (true) {
            val line = IoHelper.readLine(inputStream)
            if (line.isNotBlank()) {
                headerLines.add(line)
            } else {
                break
            }
        }
        println("******** startLine: $startLine $headerLines")
        inputStream.closeQuietly()
        return Response(Protocol.HTTP_1_1, HttpStatus.SC_OK, listOf(), null)
    }
}