package com.xiao.boot.server.base.request

import com.xiao.base.thread.KtFastThreadLocal

/**
 * @author lix wang
 */
interface RequestInfo {
    fun getRequestStartMills(): Long?
    fun setRequestStartMills(mills: Long)
    fun getExecuteStartMills(): Long?
    fun setExecuteStartMills(mills: Long)
    fun getRequestEndMills(): Long?
    fun setRequestEndMills(mills: Long)
    fun getThrowable(): Throwable?
    fun setThrowable(throwable: Throwable)
    fun getRequestUuid(): String?
    fun setRequestUuid(uuid: String)
    fun getThreadLocal(): KtFastThreadLocal<*>

    companion object {
        val KEY = object : RequestKey<RequestInfo> {}
        const val KEY_LOG_X_REQUEST_UUID = "KT-REQUEST-UUID"
    }
}