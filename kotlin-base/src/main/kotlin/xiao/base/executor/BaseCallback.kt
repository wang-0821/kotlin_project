package xiao.base.executor

import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.CompletableFuture

/**
 *
 * @author lix wang
 */
abstract class BaseCallback(
    private val callable: (() -> Any?)?,
    private val suspendCallback: (suspend () -> Any?)?,
    private val future: CompletableFuture<Any?>?,
    private val deferred: CompletableDeferred<Any?>?
) {
    open fun run() {
        checkParam()
        try {
            completeResult(callable!!.invoke())
        } catch (throwable: Throwable) {
            completeThrowable(throwable)
        }
    }

    open suspend fun suspendRun() {
        checkParam()
        try {
            completeResult(suspendCallback!!.invoke())
        } catch (throwable: Throwable) {
            completeThrowable(throwable)
        }
    }

    private fun checkParam() {
        if ((callable == null && suspendCallback == null) ||
            (callable != null && suspendCallback != null) ||
            (future == null && deferred == null) ||
            (future != null && deferred != null)
        ) {
            throw IllegalArgumentException(
                "${this::class.java.simpleName} invalid constructor params."
            )
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