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
    assert(profiles.size == 1) {
        "Environment must have only one activate profile, " +
            "current found ${profiles.joinToString { it.profileName } }."
    }
    return profiles.first()
}