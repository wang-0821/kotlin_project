package com.xiao.rpc.io

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 *
 * @author lix wang
 */
class BasicHttpEntity(private val inputStream: InputStream) : HttpEntity {
    override fun content(): InputStream {
        return inputStream
    }

    override fun contentAsString(): String {


        val charBuffer = CharArray(10240)
        val stringBuilder = StringBuilder()
        val input = BufferedReader(InputStreamReader(inputStream))
        input.readLine()
        while (true) {
            if (input.ready()) {
                while (input.read(charBuffer) != -1) {
                    stringBuilder.append(charBuffer)
                }
                break
            }
        }
        return stringBuilder.toString()
    }
}