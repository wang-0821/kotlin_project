package com.xiao.rpc.exception

import com.xiao.base.exception.HttpStatus
import com.xiao.base.exception.KtException

/**
 *
 * @author lix wang
 */
sealed class DnsException(cause: Throwable) : KtException(cause) {
    class DnsDomainResolveException(cause: Throwable) : DnsException(cause)

    companion object {
        fun dnsDomainResolveException(message: String? = null): DnsDomainResolveException {
            return DnsDomainResolveException(
                KtException().
                    statusCode(HttpStatus.SC_NOT_FOUND)
                    .message("Dns domain resolve failed $message")
            )
        }
    }
}