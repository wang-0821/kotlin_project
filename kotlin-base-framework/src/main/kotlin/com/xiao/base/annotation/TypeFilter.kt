package com.xiao.base.annotation

import com.xiao.base.resource.KtResource

/**
 *
 * @author lix wang
 */

typealias TypeFilter = (KtResource) -> Boolean

fun typeFilter(match: (KtResource) -> Boolean): TypeFilter = object : TypeFilter {
    override fun invoke(p1: KtResource): Boolean {
        return match(p1)
    }
}

val DefaultTypeFilter: TypeFilter = typeFilter {
    var result = false
    val annotations = it.annotations()
    for (annotation in annotations) {
        if (annotation.annotationClass.java.isAnnotationPresent(AnnotationScanner::class.java)) {
            result = true
            break
        }
    }
    result
}

