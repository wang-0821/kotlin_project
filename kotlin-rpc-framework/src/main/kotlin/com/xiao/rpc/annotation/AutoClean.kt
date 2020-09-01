package com.xiao.rpc.annotation

import com.xiao.base.annotation.AnnotationScan

/**
 * This annotation mark a [com.xiao.rpc.cleaner.Cleaner] effective.
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@AnnotationScan
annotation class AutoClean(
    /**
     * mills between two cleanup
     */
    val period: Int = 60 * 1000
)