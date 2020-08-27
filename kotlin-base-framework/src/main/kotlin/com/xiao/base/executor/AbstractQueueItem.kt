package com.xiao.base.executor

/**
 *
 * @author lix wang
 */
abstract class AbstractQueueItem(val name: String, val runnable: Runnable): Runnable