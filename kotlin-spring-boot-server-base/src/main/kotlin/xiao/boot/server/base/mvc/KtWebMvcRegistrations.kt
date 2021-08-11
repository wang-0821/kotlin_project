package xiao.boot.server.base.mvc

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

/**
 *
 * @author lix wang
 */
class KtWebMvcRegistrations(
    private val ktServerArgs: KtServerArgs
) : WebMvcRegistrations {
    override fun getRequestMappingHandlerAdapter(): RequestMappingHandlerAdapter? {
        return if (ktServerArgs.enableCoroutineDispatcher) {
            CoroutineRequestMappingHandlerAdapter(ktServerArgs)
        } else null
    }
}