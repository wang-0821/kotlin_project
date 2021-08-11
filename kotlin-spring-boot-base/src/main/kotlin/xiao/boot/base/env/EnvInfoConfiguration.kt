package xiao.boot.base.env

import org.springframework.context.EnvironmentAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import xiao.base.util.activeProfileType
import xiao.boot.base.ServerConstants.DEFAULT_SERVER_PORT
import xiao.boot.base.ServerConstants.SERVER_NAME_KEY
import java.net.InetAddress

/**
 *
 * @author lix wang
 */
@Configuration
class EnvInfoConfiguration : EnvironmentAware {
    private lateinit var environment: Environment

    @Bean
    fun envInfoProvider(): xiao.boot.base.env.EnvInfoProvider {
        return xiao.boot.base.env.DefaultEnvInfoProvider(
            getIp(),
            getHost(),
            getPort(),
            environment.activeProfileType(),
            environment.getProperty(SERVER_NAME_KEY) ?: "undefined"
        )
    }

    override fun setEnvironment(environment: Environment) {
        this.environment = environment
    }

    private fun getIp(): String {
        return InetAddress.getLocalHost().hostAddress
    }

    private fun getHost(): String {
        return InetAddress.getLocalHost().hostName
    }

    private fun getPort(): Int {
        return environment.getProperty("server.port", Int::class.java) ?: DEFAULT_SERVER_PORT
    }
}