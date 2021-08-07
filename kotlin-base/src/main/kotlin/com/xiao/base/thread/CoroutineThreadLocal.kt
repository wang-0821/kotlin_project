package com.xiao.base.thread

import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author lix wang
 */
internal data class KtFastThreadLocalKey(
    private val threadLocal: KtFastThreadLocal<*>
) : CoroutineContext.Key<CoroutineThreadLocal<*>>

class CoroutineThreadLocal<T>(
    private val threadLocal: KtFastThreadLocal<T>,
    private val value: T,
) : ThreadContextElement<Unit> {
    override val key: CoroutineContext.Key<CoroutineThreadLocal<*>> = KtFastThreadLocalKey(threadLocal)

    override fun restoreThreadContext(context: CoroutineContext, oldState: Unit) {
        threadLocal.reset()
    }

    override fun updateThreadContext(context: CoroutineContext) {
        threadLocal.set(value)
    }
}