package com.xiao.boot.server.base.request

import com.xiao.boot.server.base.request.RequestInfo.Companion.KEY_LOG_X_REQUEST_UUID
import kotlinx.coroutines.ThreadContextElement
import org.apache.logging.log4j.ThreadContext
import kotlin.coroutines.CoroutineContext

/**
 * @author lix wang
 */
class CoroutineLogContext(
    private val requestUuid: String
) : ThreadContextElement<Unit> {
    override val key: CoroutineContext.Key<*>
        get() = KEY

    override fun restoreThreadContext(context: CoroutineContext, oldState: Unit) {
        ThreadContext.remove(KEY_LOG_X_REQUEST_UUID)
    }

    override fun updateThreadContext(context: CoroutineContext) {
        ThreadContext.put(KEY_LOG_X_REQUEST_UUID, requestUuid)
    }

    companion object {
        val KEY = object : CoroutineContext.Key<CoroutineLogContext> {}
    }
}