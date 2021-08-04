package com.xiao.boot.base.listener

import com.xiao.boot.base.env.DefaultEnvInfoProvider
import com.xiao.boot.base.env.EnvInfoProvider
import com.xiao.boot.base.env.ProfileType
import org.springframework.boot.web.context.WebServerApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import util.serverName

/**
 * @author lix wang
 */
class SpringContextRefreshedEventListener : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val context = event.applicationContext
        val envInfoProvider = context.getBean(EnvInfoProvider::class.java)
        if (envInfoProvider != null &&
            envInfoProvider.profile() == ProfileType.TEST &&
            envInfoProvider.port() <= 0 &&
            envInfoProvider is DefaultEnvInfoProvider &&
            context is WebServerApplicationContext
        ) {
            val serverPortName = "local.${context.serverName()}.port"
            context.environment.getProperty(serverPortName)?.toInt()
                ?.let {
                    envInfoProvider.port = it
                }
        }
    }
}