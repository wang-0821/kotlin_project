package com.xiao.base.executor

import kotlinx.coroutines.CompletableDeferred

/**
 *
 * @author lix wang
 */
class CoroutineCompletableCallback<T : Any?>(
    block: (() -> T)?,
    suspendBlock: (suspend () -> T)?,
    deferred: CompletableDeferred<Any?>
) : ExecuteCallback(block, suspendBlock, null, deferred)