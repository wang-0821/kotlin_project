package com.xiao.boot.server.base.undertow

import io.undertow.Undertow
import io.undertow.servlet.api.DeploymentInfo
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.core.Ordered

/**
 * Customize undertow webServerFactory.
 *
 * @author lix wang
 */
class KtUndertowWebServerFactoryCustomizer : WebServerFactoryCustomizer<UndertowServletWebServerFactory>, Ordered {
    override fun customize(factory: UndertowServletWebServerFactory) {
        factory.addBuilderCustomizers(this::customizeWebServerBuilder)
        factory.addDeploymentInfoCustomizers(this::customizeDeploymentInfo)
    }

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }

    private fun customizeWebServerBuilder(builder: Undertow.Builder) {
        val dispatchWorkerThreads = Runtime.getRuntime().availableProcessors().coerceAtLeast(2)
        builder.setWorkerThreads(dispatchWorkerThreads)
    }

    private fun customizeDeploymentInfo(deploymentInfo: DeploymentInfo) {
        deploymentInfo.addInitialHandlerChainWrapper {
            UndertowRootInitialHttpHandler(it)
        }
    }
}