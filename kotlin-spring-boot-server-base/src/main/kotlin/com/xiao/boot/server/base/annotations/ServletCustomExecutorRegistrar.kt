package com.xiao.boot.server.base.annotations

import com.xiao.boot.server.base.properties.ServerArgs
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 * Allow servlet web application use coroutine.
 *
 * @author lix wang
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class ServletCustomExecutorRegistrar : ApplicationContextAware, InstantiationAwareBeanPostProcessor {
    private lateinit var applicationCOntext: ApplicationContext

    override fun setApplicationContext(context: ApplicationContext) {
        this.applicationCOntext = context
    }

    override fun postProcessAfterInstantiation(bean: Any, beanName: String): Boolean {
        applicationCOntext.getBean(ServerArgs::class.java)
            .apply {
                enableServletCustomExecutor = true
            }
        return true
    }
}