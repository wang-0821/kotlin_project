package com.xiao.base.executor

import kotlinx.coroutines.CompletableDeferred

/**
 *
 * @author lix wang
 */
class CoroutineCompletableCallback<T>(
    block: (() -> T)?,
    suspendBlock: (suspend () -> T)?,
    deferred: CompletableDeferred<Any?>
) : BaseCallback(block, suspendBlock, null, deferred)