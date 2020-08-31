package com.xiao.rpc.annotation

/**
 * Mark a kind of client context which implements [com.xiao.rpc.context.ClientContextAware] effective.
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ClientContext