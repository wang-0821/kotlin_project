package xiao.boot.server.base.mvc

import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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

    @Throws(Exception::class)
    override fun handleInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handlerMethod: HandlerMethod
    ): ModelAndView? {
        try {
            return super.handleInternal(request, response, handlerMethod)
        } catch (ex: Exception) {
            RequestContainer.getRequestValue(RequestInfo.KEY)?.setThrowable(ex)
            throw ex
        }
    }
}