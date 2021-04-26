package com.xiao.rpc.annotation

import com.xiao.beans.annotation.AnnotationScan

/**
 * This annotation mark a [com.xiao.rpc.cleaner.Cleaner] effective.
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@AnnotationScan
annotation class AutoClean