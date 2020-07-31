package com.xiao.rpc.io

/**
 *
 * @author lix wang
 */
abstract class AbstractConnection : Connection {
    override fun writeHeaders(request: Request) {
        var headerLine = "${request.method().name} ${request.path().orEmpty()} ${request.protocol().text}$CRLF"
        if (request.header("Host") == null) {
            request.header(Header("Host", request.host()))
        }
        if (request.header("Connection") == null) {
            request.header(Header("Connection", "Keep-Alive"))
        }
        if (request.header("Accept-Encoding") == null) {
            request.header(Header("Accept-Encoding", "gzip"))
        }
        if (request.header("User-Agent") == null) {
            request.header(Header("User-Agent", "lix-http/${System.getProperty("projectMavenVersion")}"))
        }

        for (header in request.headers()) {
            headerLine += "${header.name}: ${header.value}$CRLF"
        }
        headerLine += CRLF
        write(headerLine.toByteArray())
    }

    override fun writeBody(request: Request) {
        write(CRLF.toByteArray())
    }

    companion object {
        private const val CRLF = "\r\n"
    }
}