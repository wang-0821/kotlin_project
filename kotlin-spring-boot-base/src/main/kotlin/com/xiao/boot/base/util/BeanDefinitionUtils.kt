package com.xiao.boot.base.util

import com.xiao.boot.base.env.ProfileType
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.core.env.Environment

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