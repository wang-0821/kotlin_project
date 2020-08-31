package com.xiao.rpc.annotation

import com.xiao.base.annotation.Component

/**
 * This annotation mark a [com.xiao.rpc.cleaner.Cleaner] effective.
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Component
annotation class AutoClean(
    /**
     * mills between two cleanup
     */
    val period: Int = 60 * 1000
)