package com.xiao.boot.base.env

import com.xiao.base.util.JsonUtils
import com.xiao.boot.base.DemoEnvConfig
import com.xiao.boot.base.env.EnvConstants.DEFAULT_SERVER_PORT
import com.xiao.boot.base.util.activeProfileType
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
class EnvInfoConfiguration : EnvironmentAware {
    private lateinit var environment: Environment

    @Bean
    fun envInfoProvider(demoEnvConfig: DemoEnvConfig): EnvInfoProvider {
        println(JsonUtils.serialize(demoEnvConfig))
        return WebServerEnvInfoProvider(getIp(), getHost(), getPort(), environment.activeProfileType())
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