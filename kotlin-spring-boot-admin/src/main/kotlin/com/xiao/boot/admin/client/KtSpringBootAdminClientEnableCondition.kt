package com.xiao.boot.admin.client

import de.codecentric.boot.admin.client.config.ClientProperties
import org.springframework.boot.autoconfigure.condition.ConditionMessage
import org.springframework.boot.autoconfigure.condition.ConditionOutcome
import org.springframework.boot.autoconfigure.condition.SpringBootCondition
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata

/**
 *
 * @author lix wang
 */
class KtSpringBootAdminClientEnableCondition : SpringBootCondition() {
    override fun getMatchOutcome(context: ConditionContext, metadata: AnnotatedTypeMetadata): ConditionOutcome {
        val clientProperties = getClientProperties(context)

        if (clientProperties.isEnabled) {
            if (clientProperties.url.isNullOrEmpty() ) {
                val adminConfig = context.beanFactory!!.getBean(AdminConfig::class.java)
                clientProperties.url = arrayOf(adminConfig.adminServerUrl)
            }

            return if (clientProperties.url.isEmpty()) {
                ConditionOutcome.noMatch("Spring boot admin server url must not empty.")
            } else {
                System.getenv()["management.endpoints.web.exposure.include"] = "*"
                ConditionOutcome.match()
            }
        }
        return ConditionOutcome.noMatch(ConditionMessage.empty())
    }

    private fun getClientProperties(context: ConditionContext): ClientProperties {
        val clientProperties = ClientProperties()
        Binder.get(context.environment).bind("spring.boot.admin.client", Bindable.ofInstance(clientProperties))
        return clientProperties
    }
}