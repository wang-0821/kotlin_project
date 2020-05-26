package com.xiao.rpc.protocol

/**
 *
 * @author lix wang
 */
enum class ProtocolType(val prefix: String) {
    HTTP("http"),
    HTTPS("https");

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