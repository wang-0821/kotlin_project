package com.xiao.boot.admin.client

import de.codecentric.boot.admin.client.config.ClientProperties
import de.codecentric.boot.admin.client.config.InstanceProperties
import de.codecentric.boot.admin.client.config.SpringBootAdminClientAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration

/**
 *
 * @author lix wang
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@Conditional(KtSpringBootAdminClientEnableCondition::class)
@AutoConfigureAfter(
    WebEndpointAutoConfiguration::class,
    RestTemplateAutoConfiguration::class,
    WebClientAutoConfiguration::class
)
@EnableConfigurationProperties(
    ClientProperties::class,
    InstanceProperties::class,
    ServerProperties::class,
    ManagementServerProperties::class
)
class KtSpringBootAdminClientAutoConfiguration : SpringBootAdminClientAutoConfiguration()