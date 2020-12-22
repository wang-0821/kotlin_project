package com.xiao.base.annotation

import com.xiao.base.resource.KtClassResource

/**
 *
 * @author lix wang
 */

typealias TypeFilter = (KtClassResource) -> Boolean

object DefaultIncludeTypeFilter : TypeFilter {
    override fun invoke(p1: KtClassResource): Boolean {
        return true
    }
}

object DefaultExcludeTypeFilter : TypeFilter {
    override fun invoke(p1: KtClassResource): Boolean {
        return p1.clazz.java.isAnnotation
    }
}