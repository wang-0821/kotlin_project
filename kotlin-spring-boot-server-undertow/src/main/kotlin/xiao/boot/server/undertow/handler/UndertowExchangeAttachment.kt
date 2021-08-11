package xiao.boot.server.undertow.handler

import io.undertow.util.AttachmentKey
import xiao.boot.server.undertow.interceptor.UndertowInterceptor

/**
 *
 * @author lix wang
 */
class UndertowExchangeAttachment {
    var interceptors: List<UndertowInterceptor> = listOf()

    companion object {
        val UNDERTOW_SERVLET_ATTACHMENT: AttachmentKey<UndertowExchangeAttachment> =
            AttachmentKey.create(UndertowExchangeAttachment::class.java)
    }
}