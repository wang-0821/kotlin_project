package com.xiao.rpc.annotation

import com.xiao.base.annotation.ContextInject

/**
 * Mark a class as a [com.xiao.rpc.context.ClientContextPool].
 *
 * @author lix wang
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@ContextInject
annotation class ClientCachePool