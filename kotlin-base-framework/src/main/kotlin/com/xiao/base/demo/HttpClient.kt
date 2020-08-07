package com.xiao.base.demo

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket


/**
 *
 * @author lix wang
 */
object Main {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val addr = InetAddress.getByName("www.baidu.com")
        val socket = Socket(addr, 80)
        val autoflush = true
        val out = PrintWriter(socket.getOutputStream(), autoflush)
        val `in` = BufferedReader(
            InputStreamReader(socket.getInputStream())
        )
        // send an HTTP request to the web server
        out.println("GET / HTTP/1.1")
        out.println("Host: www.baidu.com:80")
        out.println("Connection: Close")
        out.println()
        // read the response
        var loop = true
        val sb = StringBuilder(8096)
        while (loop) {
            if (`in`.ready()) {
                var i = 0
                while (i != -1) {
                    i = `in`.read()
                    sb.append(i.toChar())
                }
                loop = false
            }
        }
        println(sb.toString())
        socket.close()
    }
}