package com.xiao.base.executor

import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture

/**
 *
 * @author lix wang
 */
class CompletableCallback(
    private val callable: Callable<Any?>,
    private val future: CompletableFuture<Any?>?,
    private val deferred: CompletableDeferred<Any?>?
) : Runnable {
    override fun run() {
        if ((future == null && deferred == null) || (future != null && deferred != null)) {
            throw IllegalArgumentException(
                "${this::class.java.simpleName} future and deferred must have one and only one not null."
            )
        }
        try {
            completeResult(callable.call())
        } catch (throwable: Throwable) {
            completeThrowable(throwable)
        }
    }

    private fun completeResult(result: Any?) {
        var completed = future?.complete(result) ?: false
        completed = completed || deferred?.complete(result) ?: false
        if (!completed) {
            throw IllegalStateException("Complete result failed.")
        }
    }

    private fun completeThrowable(throwable: Throwable) {
        future?.completeExceptionally(throwable)
        deferred?.completeExceptionally(throwable)
    }
}