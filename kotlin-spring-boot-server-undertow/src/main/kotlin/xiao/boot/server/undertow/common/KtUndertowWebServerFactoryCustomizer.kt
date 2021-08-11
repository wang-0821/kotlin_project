package xiao.boot.server.undertow.common

import io.undertow.Undertow
import io.undertow.servlet.api.DeploymentInfo
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.ApplicationContext
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import xiao.boot.server.base.mvc.KtServerArgs
import xiao.boot.server.undertow.handler.UndertowCoroutineInitialHttpHandler
import xiao.boot.server.undertow.handler.UndertowInitialHttpHandler
import xiao.boot.server.undertow.handler.UndertowInnerHttpHandler

/**
 * Customize undertow webServerFactory.
 *
 * @author lix wang
 */
@Component
class KtUndertowWebServerFactoryCustomizer(
    private val applicationContext: ApplicationContext,
    ktServerArgsProvider: ObjectProvider<KtServerArgs>
) : WebServerFactoryCustomizer<UndertowServletWebServerFactory>, Ordered {
    private var ktServerArgs: KtServerArgs? = ktServerArgsProvider.ifUnique

    override fun customize(factory: UndertowServletWebServerFactory) {
        factory.addBuilderCustomizers(this::customizeWebServerBuilder)
        factory.addDeploymentInfoCustomizers(this::customizeDeploymentInfo)
    }

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }

    private fun customizeWebServerBuilder(builder: Undertow.Builder) {
        val useCoroutineDispatcher = ktServerArgs?.enableCoroutineDispatcher ?: false
        if (useCoroutineDispatcher) {
            builder.setWorkerThreads(Runtime.getRuntime().availableProcessors().coerceAtMost(2))
        }
    }

    private fun customizeDeploymentInfo(deploymentInfo: DeploymentInfo) {
        val useCoroutineDispatcher = ktServerArgs?.enableCoroutineDispatcher ?: false

        // config initial handler
        deploymentInfo.addInitialHandlerChainWrapper {
            return@addInitialHandlerChainWrapper if (useCoroutineDispatcher) {
                UndertowCoroutineInitialHttpHandler(applicationContext, it, ktServerArgs!!)
            } else {
                UndertowInitialHttpHandler(applicationContext, it)
            }
        }

        // config inner handler
        deploymentInfo.addInnerHandlerChainWrapper {
            UndertowInnerHttpHandler(it)
        }
    }
}