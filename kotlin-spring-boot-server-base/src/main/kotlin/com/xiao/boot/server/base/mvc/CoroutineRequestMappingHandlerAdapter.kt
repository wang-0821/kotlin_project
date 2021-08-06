package com.xiao.boot.server.base.mvc

import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod

/**
 *
 * @author lix wang
 */
class CoroutineRequestMappingHandlerAdapter(
    private val serverArgs: KtServerArgs
) : RequestMappingHandlerAdapter() {
    override fun createInvocableHandlerMethod(handlerMethod: HandlerMethod): ServletInvocableHandlerMethod {
        return CoroutineServletInvocableHandlerMethod(handlerMethod)
            .apply {
                ktServerArgs = serverArgs
            }
    }
}