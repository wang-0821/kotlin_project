package com.xiao.boot.server.base.undertow

import com.xiao.boot.server.base.servlet.KtServerArgs
import io.undertow.Undertow
import io.undertow.servlet.api.DeploymentInfo
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.core.Ordered
import org.springframework.stereotype.Component

/**
 * Customize undertow webServerFactory.
 *
 * @author lix wang
 */
@Component
class KtUndertowWebServerFactoryCustomizer(
    ktServerArgsProvider: ObjectProvider<KtServerArgs>
) : WebServerFactoryCustomizer<UndertowServletWebServerFactory>, Ordered {
    private var ktServerArgs: KtServerArgs? = ktServerArgsProvider.ifUnique

    override fun customize(factory: UndertowServletWebServerFactory) {
        ktServerArgs
            ?.let {
                factory.addBuilderCustomizers(this::customizeWebServerBuilder)
                factory.addDeploymentInfoCustomizers(this::customizeDeploymentInfo)
            }
    }

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }

    private fun customizeWebServerBuilder(builder: Undertow.Builder) {
        val dispatchWorkerThreads = Runtime.getRuntime().availableProcessors().coerceAtMost(2)
        builder.setWorkerThreads(dispatchWorkerThreads)
    }

    private fun customizeDeploymentInfo(deploymentInfo: DeploymentInfo) {
        deploymentInfo.addInitialHandlerChainWrapper {
            UndertowRootInitialHttpHandler(it, ktServerArgs!!)
        }
    }
}