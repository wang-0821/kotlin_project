package com.xiao.boot.server.base.bean

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

/**
 *
 * @author lix wang
 */
class KtWebMvcRegistrations : WebMvcRegistrations {
    override fun getRequestMappingHandlerAdapter(): RequestMappingHandlerAdapter {
        return KtRequestMappingHandlerAdapter()
    }
}