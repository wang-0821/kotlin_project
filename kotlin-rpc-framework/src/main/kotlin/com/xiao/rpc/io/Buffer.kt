package com.xiao.rpc.io

import java.net.HttpURLConnection
import java.net.URL
import java.nio.ByteBuffer

/**
 *
 * @author lix wang
 */
class Buffer {
    constructor(bufferSize: Int = 0) {
        this.bufferSize = bufferSize
    }

    var bufferSize: Int
    private set(value) {
        field = if (value > 0 && field != value) {
            value
        } else {
            val maxMemory = Runtime.getRuntime().maxMemory()
            when {
                maxMemory < 64 * 1024 * 1024 ->  1024
                maxMemory < 128 * 1024 * 1024 -> 4 * 1024
                else -> 8 * 1024
            }
        }
    }

    fun buffer(): ByteBuffer = ByteBuffer.allocateDirect(bufferSize)

    fun foo() {
        val uri = "https://www.baidu.com"
        val url = URL(uri)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()
        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val text = connection.inputStream.bufferedReader().readText()
            println(text)
        }
    }
}

fun main() {
    Buffer().foo()
}