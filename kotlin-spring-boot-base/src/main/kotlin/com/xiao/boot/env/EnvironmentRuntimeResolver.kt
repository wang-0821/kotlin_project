package com.xiao.boot.env

import com.xiao.boot.env.EnvConstants.DEFAULT_SERVER_PORT
import org.springframework.context.EnvironmentAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.net.InetAddress

/**
 *
 * @author lix wang
 */
@Configuration
class EnvironmentRuntimeResolver : EnvironmentAware {
    private lateinit var environment: Environment

    @Bean
    fun envConstants(): EnvInfoProvider {
        return WebServerEnvInfoProvider(getIp(), getHost(), getPort(), getProfile())
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

    private fun getProfile(): ProfileType {
        val profiles = environment.activeProfiles
            .mapNotNullTo(HashSet()) {
                ProfileType.match(it)
            }
        assert(profiles.size == 1) {
            "Environment must have only one activate profile, " +
                "current found ${profiles.joinToString { it.profileName } }."
        }
        return profiles.first()
    }
}