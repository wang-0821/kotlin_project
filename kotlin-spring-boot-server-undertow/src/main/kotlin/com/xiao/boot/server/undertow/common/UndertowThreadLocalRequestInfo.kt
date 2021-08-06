package com.xiao.boot.server.undertow.common

import com.xiao.base.thread.KtFastThreadLocal
import com.xiao.boot.server.base.mvc.CoroutineRequestInfo
import kotlinx.coroutines.ThreadContextElement

/**
 * @author lix wang
 */
class UndertowThreadLocalRequestInfo(
    private val threadLocal: KtFastThreadLocal<UndertowRequestInfo>
) : CoroutineRequestInfo {
    override fun getRequestStartMills(): Long? {
        return threadLocal.get()?.requestStartMills
    }

    override fun setRequestStartMills(mills: Long) {
        threadLocal.get()?.requestStartMills = mills
    }

    override fun getExecuteStartMills(): Long? {
        return threadLocal.get()?.executeStartMills
    }

    override fun setExecuteStartMills(mills: Long) {
        threadLocal.get()?.executeStartMills = mills
    }

    override fun getRequestEndMills(): Long? {
        return threadLocal.get()?.requestEndMills
    }

    override fun setRequestEndMills(mills: Long) {
        threadLocal.get()?.requestEndMills = mills
    }

    override fun getThrowable(): Throwable? {
        return threadLocal.get()?.throwable
    }

    override fun setThrowable(throwable: Throwable) {
        threadLocal.get()?.throwable = throwable
    }

    override fun getRequestUuid(): String? {
        return threadLocal.get()?.requestUuid
    }

    override fun setRequestUuid(uuid: String) {
        threadLocal.get()?.requestUuid = uuid
    }

    override fun getThreadLocal(): KtFastThreadLocal<*> {
        return threadLocal
    }

    override fun getThreadContextElement(): ThreadContextElement<*>? {
        return threadLocal.get()?.threadContextElement
    }
}