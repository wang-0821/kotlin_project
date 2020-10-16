package com.xiao.demo.cases

import java.io.IOException
import java.net.ServerSocket
import java.nio.charset.Charset

/**
 * @author lix wang
 */
class Server {
    @Throws(IOException::class)
    fun runServerSingle() {
        val server = ServerSocket(8080)
        println("base socket server started.")
        // the code will block here till the request come.
        var socket = server.accept()
        var inputStream = socket.getInputStream()
        val readBytes = ByteArray(MAX_BUFFER_SIZE)
        var msgLen: Int
        val stringBuilder = StringBuilder()
        while (inputStream.read(readBytes).also { msgLen = it } != -1) {
            stringBuilder.append(String(readBytes, 0, msgLen, Charset.forName("UTF-8")))
        }
        println("get message from client: $stringBuilder")
        println("end message")
        inputStream.close()
        socket.close()
        server.close()
    }

    companion object {
        private const val MAX_BUFFER_SIZE = 1024
    }
}

fun main() {
    Server().runServerSingle()
}