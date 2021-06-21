package com.xiao.boot.base.util

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition

/**
 *
 * @author lix wang
 */
fun AnnotatedBeanDefinition.className(): String {
    return factoryMethodMetadata?.returnTypeName ?: metadata.className
}