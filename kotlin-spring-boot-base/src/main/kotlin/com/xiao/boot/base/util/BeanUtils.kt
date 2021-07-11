package com.xiao.boot.base.util

import com.xiao.boot.base.env.ProfileType
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.boot.web.context.WebServerApplicationContext
import org.springframework.core.env.Environment
import org.springframework.util.StringUtils

/**
 *
 * @author lix wang
 */
fun AnnotatedBeanDefinition.className(): String {
    return factoryMethodMetadata?.returnTypeName ?: metadata.className
}

fun Environment.activeProfileType(): ProfileType {
    val profiles = this.activeProfiles
        .mapNotNullTo(HashSet()) {
            ProfileType.match(it)
        }
    check(profiles.size == 1) {
        "Environment must have only 1 activate profile, " +
            "current found ${profiles.size}."
    }
    return profiles.first()
}

fun WebServerApplicationContext.serverName(): String {
    return if (StringUtils.hasText(this.serverNamespace)) {
        this.serverNamespace
    } else {
        "server"
    }
}