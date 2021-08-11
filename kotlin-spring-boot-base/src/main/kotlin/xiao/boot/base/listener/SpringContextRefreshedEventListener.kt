package xiao.boot.base.listener

import org.springframework.boot.web.context.WebServerApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import xiao.base.util.serverName
import xiao.boot.base.env.EnvInfoProvider
import xiao.boot.base.env.ProfileType

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
            envInfoProvider is xiao.boot.base.env.DefaultEnvInfoProvider &&
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