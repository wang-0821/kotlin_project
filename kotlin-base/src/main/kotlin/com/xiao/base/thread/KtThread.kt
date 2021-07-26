package com.xiao.base.thread

/**
 *
 * @author lix wang
 */
class KtThread : Thread, ThreadLocalValueProvider {
    constructor(runnable: Runnable, name: String) : super(runnable, name)
    constructor(runnable: Runnable) : super(runnable)
    constructor(name: String) : super(name)
    constructor(
        threadGroup: ThreadGroup?,
        runnable: Runnable,
        name: String,
        stackSize: Long
    ) : super(threadGroup, runnable, name, stackSize)

    override var indexedVariables: Array<Any?>? = null
}