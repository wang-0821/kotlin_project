package com.xiao.base.annotation

import com.xiao.base.resource.KtResource

/**
 *
 * @author lix wang
 */

typealias TypeFilter = (KtResource) -> Boolean

object DefaultIncludeTypeFilter: TypeFilter {
    override fun invoke(p1: KtResource): Boolean {
        return true
    }
}

object DefaultExcludeTypeFilter : TypeFilter {
    override fun invoke(p1: KtResource): Boolean {
        return p1.clazz.java.isAnnotation
    }
}