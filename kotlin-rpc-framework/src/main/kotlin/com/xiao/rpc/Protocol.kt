package com.xiao.rpc

/**
 *
 * @author lix wang
 */
enum class Protocol(val text: String) {
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("h2");

    companion object {
        fun parseProtocol(str: String): Protocol {
            return when (str) {
                HTTP_1_0.text -> HTTP_1_0
                HTTP_1_1.text -> HTTP_1_1
                HTTP_2.text -> HTTP_2
                else -> throw UnsupportedOperationException("Unsupported protocol $str")
            }
        }
    }
}