package xiao.base.executor

import java.util.concurrent.CompletableFuture

/**
 *
 * @author lix wang
 */
class CompletableCallback(
    callable: () -> Any?,
    future: CompletableFuture<Any?>,
) : xiao.base.executor.BaseCallback(callable, null, future, null) {
    override suspend fun suspendRun() {
        throw UnsupportedOperationException()
    }
}