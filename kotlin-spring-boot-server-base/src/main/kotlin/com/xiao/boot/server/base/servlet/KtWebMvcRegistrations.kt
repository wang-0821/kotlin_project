package com.xiao.boot.server.base.servlet

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

/**
 *
 * @author lix wang
 */
@ConditionalOnBean(CoroutineServerArgs::class)
class KtWebMvcRegistrations : WebMvcRegistrations {
    override fun getRequestMappingHandlerAdapter(): RequestMappingHandlerAdapter {
        return KtRequestMappingHandlerAdapter()
    }
}