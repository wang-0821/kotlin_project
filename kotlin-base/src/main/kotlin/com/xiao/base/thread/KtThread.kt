package com.xiao.base.thread

/**
 *
 * @author lix wang
 */
class KtThread : Thread, ThreadLocalValueProvider {
    constructor(runnable: Runnable, name: String) : super(runnable, name)
    constructor(runnable: Runnable) : super(runnable)
    constructor(name: String) : super(name)

    override var indexedVariables: Array<Any?>? = null
}