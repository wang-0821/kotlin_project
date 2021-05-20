package com.xiao.base.thread

/**
 *
 * @author lix wang
 */
class KtThread : Thread {
    constructor(runnable: Runnable, name: String) : super(runnable, name)
    constructor(runnable: Runnable) : super(runnable)
    constructor(name: String) : super(name)

    var indexedVariables: Array<Any?> = arrayOfNulls(32)
}