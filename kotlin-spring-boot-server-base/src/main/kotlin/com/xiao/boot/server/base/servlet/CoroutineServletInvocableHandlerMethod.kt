package com.xiao.boot.server.base.servlet

import org.springframework.core.KotlinDetector
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.method.HandlerMethod
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod
import java.lang.reflect.Method

/**
 * @author lix wang
 */
class CoroutineServletInvocableHandlerMethod : ServletInvocableHandlerMethod {
    internal var ktServerArgs: KtServerArgs? = null

    constructor(handler: Any, method: Method) : super(handler, method)
    constructor(handlerMethod: HandlerMethod) : super(handlerMethod)

    override fun invokeAndHandle(
        webRequest: ServletWebRequest,
        mavContainer: ModelAndViewContainer,
        vararg providedArgs: Any?
    ) {
        // todo deal with suspend method
        super.invokeAndHandle(webRequest, mavContainer, *providedArgs)
    }

    private fun isSuspendInvoke(method: Method): Boolean {
        return if (KotlinDetector.isSuspendingFunction(method)) {
            ktServerArgs
                ?.let {
                    it.enableCoroutineDispatcher && it.coroutineScope != null
                } ?: throw IllegalStateException("Suspend method ${method.name} can't find target CoroutineScope.")
        } else false
    }
}