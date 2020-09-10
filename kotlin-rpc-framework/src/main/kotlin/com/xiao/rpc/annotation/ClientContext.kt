package com.xiao.rpc.annotation

import com.xiao.base.annotation.AnnotationScan

/**
 * Mark a kind of client context which implements [com.xiao.rpc.context.ClientContextPoolAware] effective.
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@AnnotationScan
annotation class ClientContext