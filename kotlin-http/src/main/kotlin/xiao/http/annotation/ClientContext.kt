package xiao.http.annotation

import xiao.beans.annotation.AnnotationScan

/**
 * Mark a kind of client context which implements [xiao.rpc.context.ClientContextPoolAware] effective.
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@AnnotationScan
annotation class ClientContext