package com.xiao.rpc

/**
 *
 * @author lix wang
 */
enum class ProtocolType(val prefix: String, val port: Int) {
    HTTP("http", 80),
    HTTPS("https", 443);

    companion object {
        fun getType(text: String?): ProtocolType? {
            var result: ProtocolType? = null
            if (!text.isNullOrBlank()) {
                for (value in values()) {
                    if (value.prefix == text) {
                        result = value
                        break
                    }
                }
            }
            return result
        }
    }
}