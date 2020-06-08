package com.xiao.base.annotation

import com.xiao.base.resource.KtResource

/**
 *
 * @author lix wang
 */

typealias TypeFilter = (KtResource) -> Boolean

object DefaultTypeFilter: TypeFilter {
    override fun invoke(p1: KtResource): Boolean {
        return !p1.clazz.java.isInterface
                && !p1.clazz.java.isAnonymousClass
                && !p1.clazz.java.isEnum
                && !p1.clazz.java.isMemberClass
                && !p1.clazz.java.isLocalClass
    }
}