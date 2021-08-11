package xiao.http.annotation

import xiao.beans.annotation.AnnotationScan

/**
 * This annotation mark a [xiao.rpc.cleaner.Cleaner] effective.
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@AnnotationScan
annotation class AutoClean