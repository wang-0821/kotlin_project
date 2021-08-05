package com.xiao.boot.server.undertow.utils

import com.xiao.boot.server.undertow.handler.UndertowExchangeAttachment
import io.undertow.util.AttachmentKey

/**
 *
 * @author lix wang
 */
object UndertowUtils {
    val UNDERTOW_SERVLET_ATTACHMENT: AttachmentKey<UndertowExchangeAttachment> =
        AttachmentKey.create(UndertowExchangeAttachment::class.java)
}