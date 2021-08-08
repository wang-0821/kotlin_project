package com.xiao.boot.server.undertow.handler

import com.xiao.boot.server.undertow.interceptor.UndertowInterceptor
import io.undertow.util.AttachmentKey

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