package com.xiao.rpc.ssl

/**
 *
 * @author lix wang
 */
class TLSProtocols(val version: TLSVersion, val cipherSuites: List<CipherSuit>) {
    companion object {
        private val TLS_V_1_3_CIPHER_SUITES = listOf(
            CipherSuit.TLS_AES_128_CCM_8_SHA256,
            CipherSuit.TLS_AES_128_CCM_SHA256,
            CipherSuit.TLS_AES_128_GCM_SHA256,
            CipherSuit.TLS_AES_256_GCM_SHA384,
            CipherSuit.TLS_CHACHA20_POLY1305_SHA256)

        val TLS_1_3 = TLSProtocols(TLSVersion.TLS_1_3, TLS_V_1_3_CIPHER_SUITES)
    }
}