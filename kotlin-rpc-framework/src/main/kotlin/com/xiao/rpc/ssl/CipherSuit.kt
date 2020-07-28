package com.xiao.rpc.ssl

/**
 *
 * @author lix wang
 */
class CipherSuit(val description: String, val value: Int) {
    companion object {
        val TLS_AES_128_GCM_SHA256 = CipherSuit("TLS_AES_128_GCM_SHA256", 0x1301)
        val TLS_AES_256_GCM_SHA384 = CipherSuit("TLS_AES_256_GCM_SHA384", 0x1302)
        val TLS_CHACHA20_POLY1305_SHA256 = CipherSuit("TLS_CHACHA20_POLY1305_SHA256", 0x1303)
        val TLS_AES_128_CCM_SHA256 = CipherSuit("TLS_AES_128_CCM_SHA256", 0x1304)
        val TLS_AES_128_CCM_8_SHA256 = CipherSuit("TLS_AES_128_CCM_8_SHA256", 0x1305)
    }
}