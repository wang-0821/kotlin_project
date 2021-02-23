package com.xiao.base.executor

import java.util.concurrent.CompletableFuture

/**
 *
 * @author lix wang
 */
class CompletableCallback(
    callable: () -> Any?,
    future: CompletableFuture<Any?>,
) : BaseCallback(callable, null, future, null) {
    override suspend fun suspendRun() {
        throw UnsupportedOperationException()
    }
}